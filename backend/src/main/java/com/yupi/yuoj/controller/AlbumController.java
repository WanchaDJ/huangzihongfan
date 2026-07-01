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
import com.yupi.yuoj.model.dto.album.AlbumAddRequest;
import com.yupi.yuoj.model.dto.album.AlbumQueryRequest;
import com.yupi.yuoj.model.entity.Album;
import com.yupi.yuoj.service.AlbumService;
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

/**
 * 专辑接口
 */
@RestController
@RequestMapping("/album")
@Slf4j
public class AlbumController {

    @Resource
    private AlbumService albumService;

    @Resource
    private UserService userService;

    /**
     * 添加专辑（仅管理员）
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addAlbum(@RequestBody AlbumAddRequest req) {
        if (req == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Album album = new Album();
        BeanUtils.copyProperties(req, album);
        boolean result = albumService.save(album);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(album.getId());
    }

    /**
     * 删除专辑（仅管理员）
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteAlbum(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = albumService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新专辑（仅管理员）
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateAlbum(@RequestBody Album album) {
        if (album == null || album.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = albumService.updateById(album);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取专辑
     */
    @GetMapping("/get/vo")
    public BaseResponse<Album> getAlbumById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Album album = albumService.getById(id);
        ThrowUtils.throwIf(album == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(album);
    }

    /**
     * 分页获取专辑列表
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Album>> listAlbumByPage(@RequestBody AlbumQueryRequest queryRequest) {
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<Album> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(queryRequest.getSearchText())) {
            queryWrapper.like("title", queryRequest.getSearchText())
                    .or().like("artist", queryRequest.getSearchText())
                    .or().like("description", queryRequest.getSearchText());
        }
        if (StringUtils.hasText(queryRequest.getType())) {
            queryWrapper.eq("type", queryRequest.getType());
        }
        if (StringUtils.hasText(queryRequest.getArtist())) {
            queryWrapper.like("artist", queryRequest.getArtist());
        }
        queryWrapper.orderByDesc("createTime");
        Page<Album> page = albumService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(page);
    }
}
