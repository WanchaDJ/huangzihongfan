package com.yupi.yuoj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.yuoj.common.BaseResponse;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.common.ResultUtils;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.exception.ThrowUtils;
import com.yupi.yuoj.model.dto.chat.ChatMessageAddRequest;
import com.yupi.yuoj.model.entity.ChatMessage;
import com.yupi.yuoj.model.entity.User;
import com.yupi.yuoj.service.ChatMessageService;
import com.yupi.yuoj.service.UserService;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 聊天消息接口
 */
@RestController
@RequestMapping("/chat/message")
@Slf4j
public class ChatMessageController {

    @Resource
    private ChatMessageService chatMessageService;

    @Resource
    private UserService userService;

    /**
     * 发送消息（简单 AI 回复）
     */
    @PostMapping("/send")
    public BaseResponse<Map<String, Object>> sendMessage(@RequestBody ChatMessageAddRequest req,
                                                          HttpServletRequest request) {
        if (req == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        // 保存用户消息
        ChatMessage userMessage = new ChatMessage();
        userMessage.setConversationId(req.getConversationId());
        userMessage.setUserId(loginUser.getId());
        userMessage.setType(req.getType() != null ? req.getType() : "text");
        userMessage.setContent(req.getContent());
        userMessage.setFiles(req.getFiles());
        userMessage.setTimestamp(new Date());
        boolean saved = chatMessageService.save(userMessage);
        ThrowUtils.throwIf(!saved, ErrorCode.OPERATION_ERROR);
        // 生成简单 AI 回复
        String replyContent = "您好！我是AI助手，已收到您的消息：\"" + req.getContent() + "\"。请问有什么可以帮助您的？";
        ChatMessage aiMessage = new ChatMessage();
        aiMessage.setConversationId(req.getConversationId());
        aiMessage.setUserId(loginUser.getId());
        aiMessage.setType("ai");
        aiMessage.setContent(replyContent);
        aiMessage.setTimestamp(new Date());
        boolean aiSaved = chatMessageService.save(aiMessage);
        ThrowUtils.throwIf(!aiSaved, ErrorCode.OPERATION_ERROR);
        // 返回两条消息
        Map<String, Object> result = new HashMap<>();
        result.put("userMessage", userMessage);
        result.put("aiMessage", aiMessage);
        return ResultUtils.success(result);
    }

    /**
     * 获取指定会话的消息列表
     */
    @PostMapping("/list")
    public BaseResponse<List<ChatMessage>> listMessages(@RequestBody Map<String, Long> body) {
        Long conversationId = body.get("conversationId");
        if (conversationId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<ChatMessage> list = chatMessageService.list(
                new QueryWrapper<ChatMessage>()
                        .eq("conversationId", conversationId)
                        .orderByAsc("timestamp"));
        return ResultUtils.success(list);
    }
}
