package com.yupi.yuoj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yupi.yuoj.common.BaseResponse;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.common.ResultUtils;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.exception.ThrowUtils;
import com.yupi.yuoj.model.dto.usermessage.UserMessageAddRequest;
import com.yupi.yuoj.model.entity.User;
import com.yupi.yuoj.model.entity.UserMessage;
import com.yupi.yuoj.model.vo.UserMessageConversationVO;
import com.yupi.yuoj.service.UserMessageService;
import com.yupi.yuoj.service.UserService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
@Slf4j
public class UserMessageController {

    @Resource
    private UserMessageService userMessageService;

    @Resource
    private UserService userService;

    @PostMapping("/send")
    public BaseResponse<Long> sendMessage(@RequestBody UserMessageAddRequest req,
                                           HttpServletRequest request) {
        if (req == null || req.getReceiverId() == null || req.getReceiverId() <= 0
                || StringUtils.isBlank(req.getContent())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser.getId().equals(req.getReceiverId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "cannot send a message to yourself");
        }
        User receiver = userService.getById(req.getReceiverId());
        ThrowUtils.throwIf(receiver == null, ErrorCode.NOT_FOUND_ERROR);

        UserMessage message = new UserMessage();
        BeanUtils.copyProperties(req, message);
        message.setSenderId(loginUser.getId());
        message.setIsRead(0);
        message.setCreateTime(new Date());
        boolean result = userMessageService.save(message);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(message.getId());
    }

    @GetMapping("/my/list")
    public BaseResponse<List<UserMessage>> getMyMessages(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<UserMessage> list = userMessageService.list(
                new QueryWrapper<UserMessage>()
                        .eq("receiverId", loginUser.getId())
                        .orderByDesc("createTime"));
        return ResultUtils.success(list);
    }

    @GetMapping("/conversation/list")
    public BaseResponse<List<UserMessageConversationVO>> listConversations(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        List<UserMessage> messages = userMessageService.list(
                new QueryWrapper<UserMessage>()
                        .and(wrapper -> wrapper.eq("senderId", userId).or().eq("receiverId", userId))
                        .orderByDesc("createTime"));

        Map<Long, UserMessageConversationVO> conversationMap = new HashMap<>();
        for (UserMessage message : messages) {
            Long otherUserId = userId.equals(message.getSenderId()) ? message.getReceiverId() : message.getSenderId();
            UserMessageConversationVO vo = conversationMap.get(otherUserId);
            if (vo == null) {
                vo = new UserMessageConversationVO();
                vo.setOtherUserId(otherUserId);
                vo.setOtherUser(userService.getUserVO(userService.getById(otherUserId)));
                vo.setLastMessage(message);
                vo.setUnreadCount(0);
                vo.setUpdateTime(message.getCreateTime());
                conversationMap.put(otherUserId, vo);
            }
            if (userId.equals(message.getReceiverId()) && Integer.valueOf(0).equals(message.getIsRead())) {
                vo.setUnreadCount(vo.getUnreadCount() + 1);
            }
        }

        List<UserMessageConversationVO> conversations = new ArrayList<>(conversationMap.values());
        conversations.sort(Comparator.comparing(UserMessageConversationVO::getUpdateTime,
                Comparator.nullsLast(Date::compareTo)).reversed());
        return ResultUtils.success(conversations);
    }

    @GetMapping("/thread")
    public BaseResponse<List<UserMessage>> getThread(@RequestParam("otherUserId") Long otherUserId,
                                                     HttpServletRequest request) {
        if (otherUserId == null || otherUserId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        List<UserMessage> list = userMessageService.list(
                new QueryWrapper<UserMessage>()
                        .and(wrapper -> wrapper
                                .eq("senderId", userId).eq("receiverId", otherUserId)
                                .or()
                                .eq("senderId", otherUserId).eq("receiverId", userId))
                        .orderByAsc("createTime"));
        markConversationRead(userId, otherUserId);
        return ResultUtils.success(list);
    }

    @PostMapping("/read")
    public BaseResponse<Boolean> readMessage(@RequestBody Map<String, Long> body,
                                              HttpServletRequest request) {
        Long id = body == null ? null : body.get("id");
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        UserMessage message = userMessageService.getById(id);
        ThrowUtils.throwIf(message == null, ErrorCode.NOT_FOUND_ERROR);
        if (!loginUser.getId().equals(message.getReceiverId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        message.setIsRead(1);
        boolean result = userMessageService.updateById(message);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/read/conversation")
    public BaseResponse<Boolean> readConversation(@RequestBody Map<String, Long> body,
                                                  HttpServletRequest request) {
        Long otherUserId = body == null ? null : body.get("otherUserId");
        if (otherUserId == null || otherUserId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        markConversationRead(loginUser.getId(), otherUserId);
        return ResultUtils.success(true);
    }

    @GetMapping("/unread/count")
    public BaseResponse<Long> getUnreadCount(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long count = userMessageService.count(
                new QueryWrapper<UserMessage>()
                        .eq("receiverId", loginUser.getId())
                        .eq("isRead", 0));
        return ResultUtils.success(count);
    }

    private void markConversationRead(Long userId, Long otherUserId) {
        UserMessage message = new UserMessage();
        message.setIsRead(1);
        userMessageService.update(message,
                new UpdateWrapper<UserMessage>()
                        .eq("receiverId", userId)
                        .eq("senderId", otherUserId)
                        .eq("isRead", 0));
    }
}
