package com.yupi.yuoj.model.dto.news;

import com.yupi.yuoj.common.PageRequest;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 新闻查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NewsQueryRequest extends PageRequest implements Serializable {

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 标题
     */
    private String title;

    /**
     * 分类
     */
    private String category;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 发布日期
     */
    private Date publishDate;

    private static final long serialVersionUID = 1L;
}
