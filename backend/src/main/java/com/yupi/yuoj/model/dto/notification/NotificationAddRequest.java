package com.yupi.yuoj.model.dto.notification;

import java.io.Serializable;
import lombok.Data;

/**
 * 通知添加请求
 *
 */
@Data
public class NotificationAddRequest implements Serializable {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 内容
     */
    private String content;

    /**
     * 类型
     */
    private String type;

    /**
     * 关联ID
     */
    private Long relatedId;

    private static final long serialVersionUID = 1L;
}
