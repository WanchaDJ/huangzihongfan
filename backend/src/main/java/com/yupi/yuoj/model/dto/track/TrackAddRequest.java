package com.yupi.yuoj.model.dto.track;

import java.io.Serializable;
import lombok.Data;

/**
 * 音轨添加请求
 *
 */
@Data
public class TrackAddRequest implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 专辑ID
     */
    private Long albumId;

    /**
     * 专辑标题
     */
    private String albumTitle;

    /**
     * 时长（秒）
     */
    private Integer duration;

    /**
     * 时长文本
     */
    private String durationText;

    /**
     * 音轨序号
     */
    private Integer trackNumber;

    /**
     * 音频URL
     */
    private String audioUrl;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 歌词
     */
    private String lyrics;

    private static final long serialVersionUID = 1L;
}
