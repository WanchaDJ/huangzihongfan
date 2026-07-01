package com.yupi.yuoj.controller;

import com.yupi.yuoj.annotation.AuthCheck;
import com.yupi.yuoj.common.BaseResponse;
import com.yupi.yuoj.common.DeleteRequest;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.common.ResultUtils;
import com.yupi.yuoj.constant.UserConstant;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.exception.ThrowUtils;
import com.yupi.yuoj.model.dto.member.MemberBenefitAddRequest;
import com.yupi.yuoj.model.entity.MemberBenefit;
import com.yupi.yuoj.service.MemberBenefitService;
import com.yupi.yuoj.service.UserService;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会员权益接口
 */
@RestController
@RequestMapping("/member/benefit")
@Slf4j
public class MemberBenefitController {

    @Resource
    private MemberBenefitService memberBenefitService;

    @Resource
    private UserService userService;

    /**
     * 添加会员权益（仅管理员）
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addBenefit(@RequestBody MemberBenefitAddRequest req) {
        if (req == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        MemberBenefit benefit = new MemberBenefit();
        BeanUtils.copyProperties(req, benefit);
        boolean result = memberBenefitService.save(benefit);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(benefit.getId());
    }

    /**
     * 删除会员权益（仅管理员）
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteBenefit(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = memberBenefitService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 获取所有会员权益（公开）
     */
    @PostMapping("/list")
    public BaseResponse<List<MemberBenefit>> listBenefits() {
        List<MemberBenefit> list = memberBenefitService.list();
        return ResultUtils.success(list);
    }
}
