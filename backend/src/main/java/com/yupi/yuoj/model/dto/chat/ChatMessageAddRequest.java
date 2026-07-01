package com.yupi.yuoj.model.dto.chat;

import java.io.Serializable;
import lombok.Data;

/**
 * 聊天消息添加请求
 *
 */
@Data
public class ChatMessageAddRequest implements Serializable {

    /**
     * 会话ID
     */
    private Long conversationId;

    /**
     * 类型
     */
    private String type;

    /**
     * 内容
     */
    private String content;

    /**
     * 文件列表（JSON）
     */
    private String files;

    private static final long serialVersionUID = 1L;
}
