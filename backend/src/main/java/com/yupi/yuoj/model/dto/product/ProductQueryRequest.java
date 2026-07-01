package com.yupi.yuoj.model.dto.product;

import com.yupi.yuoj.common.PageRequest;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 产品查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProductQueryRequest extends PageRequest implements Serializable {

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 类型
     */
    private String type;

    /**
     * 分类
     */
    private String category;

    /**
     * 状态
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}
