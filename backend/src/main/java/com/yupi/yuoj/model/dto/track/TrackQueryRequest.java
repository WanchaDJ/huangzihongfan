package com.yupi.yuoj.model.dto.track;

import com.yupi.yuoj.common.PageRequest;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 音轨查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TrackQueryRequest extends PageRequest implements Serializable {

    /**
     * 专辑ID
     */
    private Long albumId;

    /**
     * 搜索词
     */
    private String searchText;

    private static final long serialVersionUID = 1L;
}
