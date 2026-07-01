package com.yupi.yuoj.model.vo;

import com.yupi.yuoj.model.entity.PointsRecord;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class PointsRecordVO implements Serializable {

    private Long id;
    private Long userId;
    private Integer points;
    private String description;
    private String type;
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public static PointsRecord voToObj(PointsRecordVO pointsRecordVO) {
        if (pointsRecordVO == null) {
            return null;
        }
        PointsRecord pointsRecord = new PointsRecord();
        BeanUtils.copyProperties(pointsRecordVO, pointsRecord);
        return pointsRecord;
    }

    public static PointsRecordVO objToVo(PointsRecord pointsRecord) {
        if (pointsRecord == null) {
            return null;
        }
        PointsRecordVO pointsRecordVO = new PointsRecordVO();
        BeanUtils.copyProperties(pointsRecord, pointsRecordVO);
        return pointsRecordVO;
    }
}
