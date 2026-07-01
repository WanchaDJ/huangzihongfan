package com.yupi.yuoj.model.dto.contact;

import java.io.Serializable;
import lombok.Data;

/**
 * 联系消息添加请求
 *
 */
@Data
public class ContactMessageAddRequest implements Serializable {

    /**
     * 姓名
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 主题
     */
    private String subject;

    /**
     * 消息
     */
    private String message;

    private static final long serialVersionUID = 1L;
}
