package com.yupi.yuoj.controller;

import com.yupi.yuoj.annotation.AuthCheck;
import com.yupi.yuoj.common.BaseResponse;
import com.yupi.yuoj.common.DeleteRequest;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.common.ResultUtils;
import com.yupi.yuoj.constant.UserConstant;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.exception.ThrowUtils;
import com.yupi.yuoj.model.dto.photoclass.PhotoCategoryAddRequest;
import com.yupi.yuoj.model.entity.PhotoCategory;
import com.yupi.yuoj.service.PhotoCategoryService;
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
 * 照片分类接口
 */
@RestController
@RequestMapping("/photo/category")
@Slf4j
public class PhotoCategoryController {

    @Resource
    private PhotoCategoryService photoCategoryService;

    @Resource
    private UserService userService;

    /**
     * 添加分类（仅管理员）
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addCategory(@RequestBody PhotoCategoryAddRequest req) {
        if (req == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        PhotoCategory category = new PhotoCategory();
        BeanUtils.copyProperties(req, category);
        boolean result = photoCategoryService.save(category);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(category.getId());
    }

    /**
     * 删除分类（仅管理员）
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteCategory(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = photoCategoryService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 获取所有分类（公开）
     */
    @PostMapping("/list")
    public BaseResponse<List<PhotoCategory>> listCategories() {
        List<PhotoCategory> list = photoCategoryService.list();
        return ResultUtils.success(list);
    }
}
