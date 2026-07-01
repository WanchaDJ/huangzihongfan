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
import com.yupi.yuoj.model.dto.photo.PhotoAddRequest;
import com.yupi.yuoj.model.dto.photo.PhotoQueryRequest;
import com.yupi.yuoj.model.entity.Photo;
import com.yupi.yuoj.model.entity.PhotoCategory;
import com.yupi.yuoj.model.entity.User;
import com.yupi.yuoj.service.PhotoCategoryService;
import com.yupi.yuoj.service.PhotoService;
import com.yupi.yuoj.service.UserService;
import java.util.Date;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/photo")
@Slf4j
public class PhotoController {

    private static final String USER_UPLOAD_CATEGORY = "user_upload";
    private static final String USER_UPLOAD_CATEGORY_NAME = "用户上传";

    @Resource
    private PhotoService photoService;

    @Resource
    private PhotoCategoryService photoCategoryService;

    @Resource
    private UserService userService;

    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addPhoto(@RequestBody PhotoAddRequest req) {
        if (req == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Photo photo = buildPhoto(req);
        boolean result = photoService.save(photo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(photo.getId());
    }

    @PostMapping("/my/add")
    public BaseResponse<Long> addMyPhoto(@RequestBody PhotoAddRequest req, HttpServletRequest request) {
        if (req == null || StringUtils.isBlank(req.getImageUrl())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        PhotoCategory uploadCategory = getOrCreateUserUploadCategory();
        Photo photo = buildPhoto(req);
        photo.setCategoryId(uploadCategory.getId());
        photo.setCategoryName(uploadCategory.getName());
        photo.setSubCategory(StringUtils.defaultIfBlank(req.getSubCategory(), "用户分享"));
        if (StringUtils.isBlank(photo.getTitle())) {
            photo.setTitle(loginUser.getUserName() + "的相册图片");
        }
        if (StringUtils.isBlank(photo.getDescription())) {
            photo.setDescription("由用户 " + loginUser.getUserName() + " 上传");
        }
        boolean result = photoService.save(photo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(photo.getId());
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deletePhoto(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = photoService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePhoto(@RequestBody Photo photo) {
        if (photo == null || photo.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        photo.setUpdateTime(new Date());
        boolean result = photoService.updateById(photo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @GetMapping("/get/vo")
    public BaseResponse<Photo> getPhotoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Photo photo = photoService.getById(id);
        ThrowUtils.throwIf(photo == null, ErrorCode.NOT_FOUND_ERROR);
        photo.setViewCount((photo.getViewCount() == null ? 0 : photo.getViewCount()) + 1);
        photoService.updateById(photo);
        return ResultUtils.success(photo);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Photo>> listPhotoByPage(@RequestBody PhotoQueryRequest queryRequest) {
        if (queryRequest == null) {
            queryRequest = new PhotoQueryRequest();
        }
        long current = queryRequest.getCurrent() <= 0 ? 1 : queryRequest.getCurrent();
        long size = queryRequest.getPageSize() <= 0 ? 12 : queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        QueryWrapper<Photo> queryWrapper = new QueryWrapper<>();
        if (queryRequest.getCategoryId() != null) {
            queryWrapper.eq("categoryId", queryRequest.getCategoryId());
        }
        if (StringUtils.isNotBlank(queryRequest.getCategoryName())) {
            queryWrapper.eq("categoryName", queryRequest.getCategoryName());
        }
        if (StringUtils.isNotBlank(queryRequest.getSubCategory())) {
            queryWrapper.eq("subCategory", queryRequest.getSubCategory());
        }
        String searchText = queryRequest.getSearchText();
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(wrapper -> wrapper.like("title", searchText)
                    .or().like("description", searchText));
        }
        queryWrapper.orderByDesc("createTime");
        Page<Photo> page = photoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(page);
    }

    private Photo buildPhoto(PhotoAddRequest req) {
        Photo photo = new Photo();
        BeanUtils.copyProperties(req, photo);
        Date now = new Date();
        if (StringUtils.isBlank(photo.getTitle())) {
            photo.setTitle("未命名图片");
        }
        if (StringUtils.isBlank(photo.getThumbnailUrl())) {
            photo.setThumbnailUrl(photo.getImageUrl());
        }
        if (photo.getSortOrder() == null) {
            photo.setSortOrder(0);
        }
        if (photo.getLikeCount() == null) {
            photo.setLikeCount(0);
        }
        if (photo.getViewCount() == null) {
            photo.setViewCount(0);
        }
        photo.setCreateTime(now);
        photo.setUpdateTime(now);
        return photo;
    }

    private PhotoCategory getOrCreateUserUploadCategory() {
        PhotoCategory category = photoCategoryService.getOne(
                new QueryWrapper<PhotoCategory>().eq("name", USER_UPLOAD_CATEGORY_NAME).last("limit 1"));
        if (category != null) {
            return category;
        }
        Date now = new Date();
        category = new PhotoCategory();
        category.setName(USER_UPLOAD_CATEGORY_NAME);
        category.setDescription(USER_UPLOAD_CATEGORY);
        category.setSortOrder(99);
        category.setCreateTime(now);
        category.setUpdateTime(now);
        boolean saved = photoCategoryService.save(category);
        ThrowUtils.throwIf(!saved, ErrorCode.OPERATION_ERROR);
        return category;
    }
}
