package com.yupi.yuoj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yuoj.common.BaseResponse;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.common.ResultUtils;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.exception.ThrowUtils;
import com.yupi.yuoj.model.entity.ConsumptionRecord;
import com.yupi.yuoj.model.entity.User;
import com.yupi.yuoj.service.ConsumptionRecordService;
import com.yupi.yuoj.service.UserService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * 消费记录接口
 */
@RestController
@RequestMapping("/member/consumption")
@Slf4j
public class ConsumptionRecordController {

    @Resource
    private ConsumptionRecordService consumptionRecordService;

    @Resource
    private UserService userService;

    /**
     * 分页获取当前用户的消费记录
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<ConsumptionRecord>> getMyConsumptionRecords(
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        long current = body.get("current") != null ? ((Number) body.get("current")).longValue() : 1;
        long pageSize = body.get("pageSize") != null ? ((Number) body.get("pageSize")).longValue() : 10;
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        Page<ConsumptionRecord> page = consumptionRecordService.page(
                new Page<>(current, pageSize),
                new QueryWrapper<ConsumptionRecord>()
                        .eq("userId", loginUser.getId())
                        .orderByDesc("createTime"));
        return ResultUtils.success(page);
    }
}
