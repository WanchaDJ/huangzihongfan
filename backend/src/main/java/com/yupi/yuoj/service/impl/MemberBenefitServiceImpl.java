package com.yupi.yuoj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yuoj.mapper.MemberBenefitMapper;
import com.yupi.yuoj.model.entity.MemberBenefit;
import com.yupi.yuoj.service.MemberBenefitService;
import org.springframework.stereotype.Service;

@Service
public class MemberBenefitServiceImpl extends ServiceImpl<MemberBenefitMapper, MemberBenefit> implements MemberBenefitService {
}
