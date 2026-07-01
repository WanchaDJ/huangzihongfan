package com.yupi.yuoj.model.dto.product;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 产品添加请求
 *
 */
@Data
public class ProductAddRequest implements Serializable {

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 类型
     */
    private String type;

    /**
     * 分类
     */
    private String category;

    private static final long serialVersionUID = 1L;
}
