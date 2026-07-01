package com.yupi.yuoj.model.dto.member;

import java.io.Serializable;
import lombok.Data;

/**
 * 会员权益添加请求
 *
 */
@Data
public class MemberBenefitAddRequest implements Serializable {

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 图标
     */
    private String icon;

    /**
     * 适用等级（JSON）
     */
    private String levels;

    private static final long serialVersionUID = 1L;
}
