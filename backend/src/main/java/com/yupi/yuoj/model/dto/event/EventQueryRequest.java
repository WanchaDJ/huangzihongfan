package com.yupi.yuoj.model.dto.event;

import com.yupi.yuoj.common.PageRequest;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 活动查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EventQueryRequest extends PageRequest implements Serializable {

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 城市
     */
    private String city;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 类型
     */
    private String type;

    private static final long serialVersionUID = 1L;
}
