package com.yupi.yuoj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yuoj.mapper.TrackMapper;
import com.yupi.yuoj.model.entity.Track;
import com.yupi.yuoj.service.TrackService;
import org.springframework.stereotype.Service;

@Service
public class TrackServiceImpl extends ServiceImpl<TrackMapper, Track> implements TrackService {
}
