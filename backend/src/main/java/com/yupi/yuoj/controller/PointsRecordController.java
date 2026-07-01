package com.yupi.yuoj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yuoj.common.BaseResponse;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.common.ResultUtils;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.exception.ThrowUtils;
import com.yupi.yuoj.model.entity.PointsRecord;
import com.yupi.yuoj.model.entity.User;
import com.yupi.yuoj.model.entity.UserProfile;
import com.yupi.yuoj.service.PointsRecordService;
import com.yupi.yuoj.service.UserProfileService;
import com.yupi.yuoj.service.UserService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * 积分记录接口
 */
@RestController
@RequestMapping("/member/points")
@Slf4j
public class PointsRecordController {

    @Resource
    private PointsRecordService pointsRecordService;

    @Resource
    private UserService userService;

    @Resource
    private UserProfileService userProfileService;

    /**
     * 分页获取当前用户的积分记录
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<PointsRecord>> getMyPointsRecords(
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        long current = body.get("current") != null ? ((Number) body.get("current")).longValue() : 1;
        long pageSize = body.get("pageSize") != null ? ((Number) body.get("pageSize")).longValue() : 10;
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        Page<PointsRecord> page = pointsRecordService.page(
                new Page<>(current, pageSize),
                new QueryWrapper<PointsRecord>()
                        .eq("userId", loginUser.getId())
                        .orderByDesc("createTime"));
        return ResultUtils.success(page);
    }

    /**
     * 获取当前用户积分总数
     */
    @GetMapping("/my/total")
    public BaseResponse<Integer> getMyPointsTotal(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        UserProfile profile = userProfileService.getOne(
                new QueryWrapper<UserProfile>().eq("userId", loginUser.getId()));
        if (profile == null) {
            return ResultUtils.success(0);
        }
        return ResultUtils.success(profile.getPoints() != null ? profile.getPoints() : 0);
    }
}
