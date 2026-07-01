package com.yupi.yuoj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yuoj.annotation.AuthCheck;
import com.yupi.yuoj.common.BaseResponse;
import com.yupi.yuoj.common.DeleteRequest;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.common.ResultUtils;
import com.yupi.yuoj.constant.UserConstant;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.exception.ThrowUtils;
import com.yupi.yuoj.model.dto.event.EventAddRequest;
import com.yupi.yuoj.model.dto.event.EventQueryRequest;
import com.yupi.yuoj.model.entity.Event;
import com.yupi.yuoj.model.vo.EventVO;
import com.yupi.yuoj.service.EventService;
import com.yupi.yuoj.service.UserService;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 活动接口
 */
@RestController
@RequestMapping("/event")
@Slf4j
public class EventController {

    @Resource
    private EventService eventService;

    @Resource
    private UserService userService;

    /**
     * 添加活动（仅管理员）
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addEvent(@RequestBody EventAddRequest req) {
        if (req == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Event event = new Event();
        BeanUtils.copyProperties(req, event);
        boolean result = eventService.save(event);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(event.getId());
    }

    /**
     * 删除活动（仅管理员）
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteEvent(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = eventService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新活动（仅管理员）
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateEvent(@RequestBody Event event) {
        if (event == null || event.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = eventService.updateById(event);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取活动
     */
    @GetMapping("/get/vo")
    public BaseResponse<EventVO> getEventById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Event event = eventService.getById(id);
        ThrowUtils.throwIf(event == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(convertToVO(event));
    }

    /**
     * 分页获取活动列表
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<EventVO>> listEventByPage(@RequestBody EventQueryRequest queryRequest) {
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<Event> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(queryRequest.getSearchText())) {
            queryWrapper.like("title", queryRequest.getSearchText())
                    .or().like("description", queryRequest.getSearchText());
        }
        if (StringUtils.hasText(queryRequest.getCity())) {
            queryWrapper.eq("city", queryRequest.getCity());
        }
        if (queryRequest.getStatus() != null) {
            queryWrapper.eq("status", queryRequest.getStatus());
        }
        if (StringUtils.hasText(queryRequest.getType())) {
            queryWrapper.eq("type", queryRequest.getType());
        }
        queryWrapper.orderByDesc("createTime");
        Page<Event> eventPage = eventService.page(new Page<>(current, size), queryWrapper);
        
        // 转换为VO并处理status字段
        Page<EventVO> voPage = new Page<>(current, size, eventPage.getTotal());
        List<EventVO> voList = eventPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return ResultUtils.success(voPage);
    }
    
    /**
     * 将Event实体转换为EventVO，并处理status字段
     */
    private EventVO convertToVO(Event event) {
        EventVO vo = new EventVO();
        BeanUtils.copyProperties(event, vo);
        // 统一status字段为大写格式
        if (event.getStatus() != null) {
            vo.setStatus(normalizeStatus(event.getStatus()));
        }
        return vo;
    }
    
    /**
     * 标准化状态字段格式
     */
    private String normalizeStatus(String status) {
        if (status == null) {
            return "UPCOMING";
        }
        String lowerStatus = status.toLowerCase();
        switch (lowerStatus) {
            case "onsale":
            case "on_sale":
            case "on-sale":
                return "ON_SALE";
            case "upcoming":
                return "UPCOMING";
            case "soldout":
            case "sold_out":
            case "sold-out":
                return "SOLD_OUT";
            case "ended":
                return "ENDED";
            default:
                return status.toUpperCase();
        }
    }
}
