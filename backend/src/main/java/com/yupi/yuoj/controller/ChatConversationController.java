package com.yupi.yuoj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.yuoj.common.BaseResponse;
import com.yupi.yuoj.common.DeleteRequest;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.common.ResultUtils;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.exception.ThrowUtils;
import com.yupi.yuoj.model.entity.ChatConversation;
import com.yupi.yuoj.model.entity.User;
import com.yupi.yuoj.service.ChatConversationService;
import com.yupi.yuoj.service.UserService;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 聊天会话接口
 */
@RestController
@RequestMapping("/chat/conversation")
@Slf4j
public class ChatConversationController {

    @Resource
    private ChatConversationService chatConversationService;

    @Resource
    private UserService userService;

    /**
     * 创建会话
     */
    @PostMapping("/create")
    public BaseResponse<Long> createConversation(@RequestBody(required = false) Map<String, Object> body,
                                                  HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        String title = (body != null && body.get("title") != null) ? body.get("title").toString() : "新对话";
        ChatConversation conversation = new ChatConversation();
        conversation.setUserId(loginUser.getId());
        conversation.setTitle(title);
        conversation.setCreatedAt(new Date());
        conversation.setUpdatedAt(new Date());
        boolean result = chatConversationService.save(conversation);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(conversation.getId());
    }

    /**
     * 获取当前用户的会话列表
     */
    @GetMapping("/my/list")
    public BaseResponse<List<ChatConversation>> getMyConversations(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<ChatConversation> list = chatConversationService.list(
                new QueryWrapper<ChatConversation>()
                        .eq("userId", loginUser.getId())
                        .orderByDesc("updatedAt"));
        return ResultUtils.success(list);
    }

    /**
     * 删除会话（验证归属）
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteConversation(@RequestBody DeleteRequest deleteRequest,
                                                     HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        ChatConversation conversation = chatConversationService.getById(id);
        ThrowUtils.throwIf(conversation == null, ErrorCode.NOT_FOUND_ERROR);
        if (!conversation.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = chatConversationService.removeById(id);
        return ResultUtils.success(b);
    }
}
