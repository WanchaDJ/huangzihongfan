package com.yupi.yuoj.model.dto.news;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 新闻添加请求
 *
 */
@Data
public class NewsAddRequest implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 分类
     */
    private String category;

    /**
     * 发布日期
     */
    private Date publishDate;

    private static final long serialVersionUID = 1L;
}
