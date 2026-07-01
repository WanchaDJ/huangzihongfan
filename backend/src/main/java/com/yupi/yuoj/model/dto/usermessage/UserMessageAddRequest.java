package com.yupi.yuoj.model.dto.usermessage;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户消息添加请求
 *
 */
@Data
public class UserMessageAddRequest implements Serializable {

    /**
     * 接收者ID
     */
    private Long receiverId;

    /**
     * 内容
     */
    private String content;

    private static final long serialVersionUID = 1L;
}
