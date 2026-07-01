package com.yupi.yuoj.model.dto.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * 订单创建请求
 *
 */
@Data
public class OrderCreateRequest implements Serializable {

    /**
     * 订单项列表
     */
    private List<OrderItemRequest> items;

    /**
     * 订单项
     */
    @Data
    public static class OrderItemRequest implements Serializable {

        /**
         * 产品ID
         */
        private Long productId;

        /**
         * 名称
         */
        private String name;

        /**
         * 规格
         */
        private String spec;

        /**
         * 价格
         */
        private BigDecimal price;

        /**
         * 数量
         */
        private Integer quantity;

        /**
         * 图片
         */
        private String image;

        private static final long serialVersionUID = 1L;
    }

    private static final long serialVersionUID = 1L;
}
