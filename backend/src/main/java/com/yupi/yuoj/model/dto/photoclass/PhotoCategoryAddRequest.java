package com.yupi.yuoj.model.dto.photoclass;

import java.io.Serializable;
import lombok.Data;

/**
 * 照片分类添加请求
 *
 */
@Data
public class PhotoCategoryAddRequest implements Serializable {

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    private static final long serialVersionUID = 1L;
}
