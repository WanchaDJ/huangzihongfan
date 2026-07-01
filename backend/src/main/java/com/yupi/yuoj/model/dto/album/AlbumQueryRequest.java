package com.yupi.yuoj.model.dto.album;

import com.yupi.yuoj.common.PageRequest;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 专辑查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AlbumQueryRequest extends PageRequest implements Serializable {

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 类型
     */
    private String type;

    /**
     * 艺人
     */
    private String artist;

    private static final long serialVersionUID = 1L;
}
