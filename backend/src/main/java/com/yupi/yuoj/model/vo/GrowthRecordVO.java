package com.yupi.yuoj.model.vo;

import com.yupi.yuoj.model.entity.GrowthRecord;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class GrowthRecordVO implements Serializable {

    private Long id;
    private Long userId;
    private Integer value;
    private String reason;
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public static GrowthRecord voToObj(GrowthRecordVO growthRecordVO) {
        if (growthRecordVO == null) {
            return null;
        }
        GrowthRecord growthRecord = new GrowthRecord();
        BeanUtils.copyProperties(growthRecordVO, growthRecord);
        return growthRecord;
    }

    public static GrowthRecordVO objToVo(GrowthRecord growthRecord) {
        if (growthRecord == null) {
            return null;
        }
        GrowthRecordVO growthRecordVO = new GrowthRecordVO();
        BeanUtils.copyProperties(growthRecord, growthRecordVO);
        return growthRecordVO;
    }
}
