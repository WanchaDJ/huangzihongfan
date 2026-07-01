package com.yupi.yuoj.model.dto.order;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 演出订单创建请求
 */
@Data
public class EventOrderCreateRequest implements Serializable {

    /**
     * 演出ID
     */
    private Long eventId;

    /**
     * 票价
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 票价档位描述
     */
    private String spec;

    private static final long serialVersionUID = 1L;
}