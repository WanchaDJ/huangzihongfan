package com.yupi.yuoj.model.dto.chat;

import java.io.Serializable;
import lombok.Data;

/**
 * 聊天会话添加请求
 *
 */
@Data
public class ChatConversationAddRequest implements Serializable {

    /**
     * 标题
     */
    private String title;

    private static final long serialVersionUID = 1L;
}
