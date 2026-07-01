package com.yupi.yuoj.model.vo;

import com.yupi.yuoj.model.entity.OrderInfo;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class OrderInfoVO implements Serializable {

    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private Integer itemCount;
    private String status;
    private String statusText;
    private String remark;
    private Date payTime;
    private Date createTime;
    private Date updateTime;

    private List<OrderItemVO> items;

    private static final long serialVersionUID = 1L;

    public static OrderInfo voToObj(OrderInfoVO orderInfoVO) {
        if (orderInfoVO == null) {
            return null;
        }
        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(orderInfoVO, orderInfo);
        return orderInfo;
    }

    public static OrderInfoVO objToVo(OrderInfo orderInfo) {
        if (orderInfo == null) {
            return null;
        }
        OrderInfoVO orderInfoVO = new OrderInfoVO();
        BeanUtils.copyProperties(orderInfo, orderInfoVO);
        return orderInfoVO;
    }
}
