package com.yupi.yuoj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.yuoj.common.BaseResponse;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.common.ResultUtils;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.exception.ThrowUtils;
import com.yupi.yuoj.model.entity.GrowthRecord;
import com.yupi.yuoj.model.entity.User;
import com.yupi.yuoj.service.GrowthRecordService;
import com.yupi.yuoj.service.UserService;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 成长记录接口
 */
@RestController
@RequestMapping("/member/growth")
@Slf4j
public class GrowthRecordController {

    @Resource
    private GrowthRecordService growthRecordService;

    @Resource
    private UserService userService;

    /**
     * 获取当前用户的成长记录
     */
    @GetMapping("/my/list")
    public BaseResponse<List<GrowthRecord>> getMyGrowthRecords(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<GrowthRecord> records = growthRecordService.list(
                new QueryWrapper<GrowthRecord>()
                        .eq("userId", loginUser.getId())
                        .orderByDesc("createTime"));
        return ResultUtils.success(records);
    }

    /**
     * 添加成长记录（系统内部使用）
     */
    @PostMapping("/add")
    public BaseResponse<Long> addGrowthRecord(@RequestBody GrowthRecord growthRecord) {
        if (growthRecord == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = growthRecordService.save(growthRecord);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(growthRecord.getId());
    }
}
