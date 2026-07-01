package com.yupi.yuoj.model.dto.album;

import java.io.Serializable;
import lombok.Data;

/**
 * 专辑添加请求
 *
 */
@Data
public class AlbumAddRequest implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 艺人
     */
    private String artist;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 发行年份
     */
    private Integer releaseYear;

    /**
     * 类型
     */
    private String type;

    /**
     * 音轨数量
     */
    private Integer trackCount;

    /**
     * 描述
     */
    private String description;

    private static final long serialVersionUID = 1L;
}
