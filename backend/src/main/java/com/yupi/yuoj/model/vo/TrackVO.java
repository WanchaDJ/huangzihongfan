package com.yupi.yuoj.model.vo;

import com.yupi.yuoj.model.entity.Track;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class TrackVO implements Serializable {

    private Long id;
    private String title;
    private Long albumId;
    private String albumTitle;
    private Integer duration;
    private String durationText;
    private Integer trackNumber;
    private String audioUrl;
    private String coverImage;
    private String lyrics;
    private Integer playCount;
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public static Track voToObj(TrackVO trackVO) {
        if (trackVO == null) {
            return null;
        }
        Track track = new Track();
        BeanUtils.copyProperties(trackVO, track);
        return track;
    }

    public static TrackVO objToVo(Track track) {
        if (track == null) {
            return null;
        }
        TrackVO trackVO = new TrackVO();
        BeanUtils.copyProperties(track, trackVO);
        return trackVO;
    }
}
