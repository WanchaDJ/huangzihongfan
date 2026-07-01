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
import com.yupi.yuoj.model.dto.track.TrackAddRequest;
import com.yupi.yuoj.model.dto.track.TrackQueryRequest;
import com.yupi.yuoj.model.entity.Track;
import com.yupi.yuoj.service.TrackService;
import com.yupi.yuoj.service.UserService;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 音轨接口
 */
@RestController
@RequestMapping("/track")
@Slf4j
public class TrackController {

    @Resource
    private TrackService trackService;

    @Resource
    private UserService userService;

    /**
     * 添加音轨（仅管理员）
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addTrack(@RequestBody TrackAddRequest req) {
        if (req == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Track track = new Track();
        BeanUtils.copyProperties(req, track);
        boolean result = trackService.save(track);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(track.getId());
    }

    /**
     * 删除音轨（仅管理员）
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteTrack(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = trackService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新音轨（仅管理员）
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateTrack(@RequestBody Track track) {
        if (track == null || track.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = trackService.updateById(track);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取音轨
     */
    @GetMapping("/get/vo")
    public BaseResponse<Track> getTrackById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Track track = trackService.getById(id);
        ThrowUtils.throwIf(track == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(track);
    }

    /**
     * 分页获取音轨列表（可按专辑ID筛选）
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Track>> listTrackByPage(@RequestBody TrackQueryRequest queryRequest) {
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<Track> queryWrapper = new QueryWrapper<>();
        if (queryRequest.getAlbumId() != null) {
            queryWrapper.eq("albumId", queryRequest.getAlbumId());
        }
        if (queryRequest.getSearchText() != null && !queryRequest.getSearchText().isEmpty()) {
            queryWrapper.like("title", queryRequest.getSearchText());
        }
        queryWrapper.orderByDesc("createTime");
        Page<Track> page = trackService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(page);
    }
}
