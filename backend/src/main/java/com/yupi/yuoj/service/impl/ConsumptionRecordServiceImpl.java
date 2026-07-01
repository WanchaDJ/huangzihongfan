package com.yupi.yuoj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yuoj.mapper.ConsumptionRecordMapper;
import com.yupi.yuoj.model.entity.ConsumptionRecord;
import com.yupi.yuoj.service.ConsumptionRecordService;
import org.springframework.stereotype.Service;

@Service
public class ConsumptionRecordServiceImpl extends ServiceImpl<ConsumptionRecordMapper, ConsumptionRecord> implements ConsumptionRecordService {
}
