package com.yupi.yuoj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yuoj.mapper.GrowthRecordMapper;
import com.yupi.yuoj.model.entity.GrowthRecord;
import com.yupi.yuoj.service.GrowthRecordService;
import org.springframework.stereotype.Service;

@Service
public class GrowthRecordServiceImpl extends ServiceImpl<GrowthRecordMapper, GrowthRecord> implements GrowthRecordService {
}
