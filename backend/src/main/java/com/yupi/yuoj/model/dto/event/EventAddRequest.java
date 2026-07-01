package com.yupi.yuoj.model.dto.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 活动添加请求
 *
 */
@Data
public class EventAddRequest implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 城市
     */
    private String city;

    /**
     * 场馆
     */
    private String venue;

    /**
     * 活动日期
     */
    private Date eventDate;

    /**
     * 开售时间
     */
    private Date saleStartTime;

    /**
     * 价格档位（JSON）
     */
    private String priceTiers;

    /**
     * 最低价格
     */
    private BigDecimal minPrice;

    /**
     * 最高价格
     */
    private BigDecimal maxPrice;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 描述
     */
    private String description;

    /**
     * 类型
     */
    private String type;

    private static final long serialVersionUID = 1L;
}
