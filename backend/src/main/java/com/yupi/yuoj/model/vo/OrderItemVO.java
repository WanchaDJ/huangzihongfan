package com.yupi.yuoj.model.vo;

import com.yupi.yuoj.model.entity.OrderItem;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class OrderItemVO implements Serializable {

    private Long id;
    private Long orderId;
    private Long productId;
    private String name;
    private String spec;
    private BigDecimal price;
    private Integer quantity;
    private String image;

    private static final long serialVersionUID = 1L;

    public static OrderItem voToObj(OrderItemVO orderItemVO) {
        if (orderItemVO == null) {
            return null;
        }
        OrderItem orderItem = new OrderItem();
        BeanUtils.copyProperties(orderItemVO, orderItem);
        return orderItem;
    }

    public static OrderItemVO objToVo(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        OrderItemVO orderItemVO = new OrderItemVO();
        BeanUtils.copyProperties(orderItem, orderItemVO);
        return orderItemVO;
    }
}
