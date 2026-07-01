package com.yupi.yuoj.model.vo;

import com.yupi.yuoj.model.entity.ConsumptionRecord;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class ConsumptionRecordVO implements Serializable {

    private Long id;
    private Long userId;
    private BigDecimal amount;
    private String description;
    private String type;
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public static ConsumptionRecord voToObj(ConsumptionRecordVO consumptionRecordVO) {
        if (consumptionRecordVO == null) {
            return null;
        }
        ConsumptionRecord consumptionRecord = new ConsumptionRecord();
        BeanUtils.copyProperties(consumptionRecordVO, consumptionRecord);
        return consumptionRecord;
    }

    public static ConsumptionRecordVO objToVo(ConsumptionRecord consumptionRecord) {
        if (consumptionRecord == null) {
            return null;
        }
        ConsumptionRecordVO consumptionRecordVO = new ConsumptionRecordVO();
        BeanUtils.copyProperties(consumptionRecord, consumptionRecordVO);
        return consumptionRecordVO;
    }
}
