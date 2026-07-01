package com.yupi.yuoj.model.vo;

import com.yupi.yuoj.model.entity.Event;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class EventVO implements Serializable {

    private Long id;
    private String title;
    private String description;
    private String coverImage;
    private Date eventDate;
    private String city;
    private String venue;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer stock;
    private Integer soldCount;
    private String status;
    private Date createTime;
    private Date updateTime;
    // 添加缺失字段
    private String priceTiers;
    private Date saleStartTime;
    private String type;

    private static final long serialVersionUID = 1L;

    public static Event voToObj(EventVO eventVO) {
        if (eventVO == null) {
            return null;
        }
        Event event = new Event();
        BeanUtils.copyProperties(eventVO, event);
        return event;
    }

    public static EventVO objToVo(Event event) {
        if (event == null) {
            return null;
        }
        EventVO eventVO = new EventVO();
        BeanUtils.copyProperties(event, eventVO);
        return eventVO;
    }
}
