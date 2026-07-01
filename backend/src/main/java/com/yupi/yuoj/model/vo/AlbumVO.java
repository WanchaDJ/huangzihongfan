package com.yupi.yuoj.model.vo;

import com.yupi.yuoj.model.entity.Album;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class AlbumVO implements Serializable {

    private Long id;
    private String title;
    private String artist;
    private String coverImage;
    private Integer releaseYear;
    private String type;
    private Integer trackCount;
    private String description;
    private Integer playCount;
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public static Album voToObj(AlbumVO albumVO) {
        if (albumVO == null) {
            return null;
        }
        Album album = new Album();
        BeanUtils.copyProperties(albumVO, album);
        return album;
    }

    public static AlbumVO objToVo(Album album) {
        if (album == null) {
            return null;
        }
        AlbumVO albumVO = new AlbumVO();
        BeanUtils.copyProperties(album, albumVO);
        return albumVO;
    }
}
