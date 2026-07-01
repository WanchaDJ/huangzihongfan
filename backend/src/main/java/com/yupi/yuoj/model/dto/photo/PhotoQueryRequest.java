package com.yupi.yuoj.model.dto.photo;

import com.yupi.yuoj.common.PageRequest;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 照片查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PhotoQueryRequest extends PageRequest implements Serializable {

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

    /**
     * 搜索词
     */
    private String searchText;

    private static final long serialVersionUID = 1L;
}
