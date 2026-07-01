package com.yupi.yuoj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.yuoj.common.BaseResponse;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.common.ResultUtils;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.exception.ThrowUtils;
import com.yupi.yuoj.model.entity.Notification;
import com.yupi.yuoj.model.entity.User;
import com.yupi.yuoj.service.NotificationService;
import com.yupi.yuoj.service.UserService;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通知接口
 */
@RestController
@RequestMapping("/notification")
@Slf4j
public class NotificationController {

    @Resource
    private NotificationService notificationService;

    @Resource
    private UserService userService;

    /**
     * 获取当前用户的通知列表
     */
    @GetMapping("/my/list")
    public BaseResponse<List<Notification>> getMyNotifications(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<Notification> list = notificationService.list(
                new QueryWrapper<Notification>()
                        .eq("userId", loginUser.getId())
                        .orderByDesc("createTime"));
        return ResultUtils.success(list);
    }

    /**
     * 标记通知为已读
     */
    @PostMapping("/read")
    public BaseResponse<Boolean> readNotification(@RequestBody Map<String, Long> body,
                                                   HttpServletRequest request) {
        Long id = body.get("id");
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Notification notification = notificationService.getById(id);
        ThrowUtils.throwIf(notification == null, ErrorCode.NOT_FOUND_ERROR);
        notification.setIsRead(1);
        boolean result = notificationService.updateById(notification);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }
}
