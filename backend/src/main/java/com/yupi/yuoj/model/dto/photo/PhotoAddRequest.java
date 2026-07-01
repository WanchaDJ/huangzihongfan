package com.yupi.yuoj.model.dto.photo;

import java.io.Serializable;
import lombok.Data;

/**
 * 照片添加请求
 *
 */
@Data
public class PhotoAddRequest implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 子分类
     */
    private String subCategory;

    private static final long serialVersionUID = 1L;
}
