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
import com.yupi.yuoj.model.dto.product.ProductAddRequest;
import com.yupi.yuoj.model.dto.product.ProductQueryRequest;
import com.yupi.yuoj.model.entity.Product;
import com.yupi.yuoj.service.ProductService;
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
 * 产品接口
 */
@RestController
@RequestMapping("/product")
@Slf4j
public class ProductController {

    @Resource
    private ProductService productService;

    @Resource
    private UserService userService;

    /**
     * 添加产品（仅管理员）
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addProduct(@RequestBody ProductAddRequest req) {
        if (req == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Product product = new Product();
        BeanUtils.copyProperties(req, product);
        boolean result = productService.save(product);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(product.getId());
    }

    /**
     * 删除产品（仅管理员）
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteProduct(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = productService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新产品（仅管理员）
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateProduct(@RequestBody Product product) {
        if (product == null || product.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = productService.updateById(product);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取产品
     */
    @GetMapping("/get/vo")
    public BaseResponse<Product> getProductById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Product product = productService.getById(id);
        ThrowUtils.throwIf(product == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(product);
    }

    /**
     * 分页获取产品列表
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Product>> listProductByPage(@RequestBody ProductQueryRequest queryRequest) {
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(queryRequest.getSearchText())) {
            queryWrapper.like("name", queryRequest.getSearchText())
                    .or().like("description", queryRequest.getSearchText());
        }
        if (StringUtils.hasText(queryRequest.getType())) {
            queryWrapper.eq("type", queryRequest.getType());
        }
        if (StringUtils.hasText(queryRequest.getCategory())) {
            queryWrapper.eq("category", queryRequest.getCategory());
        }
        if (queryRequest.getStatus() != null) {
            queryWrapper.eq("status", queryRequest.getStatus());
        }
        queryWrapper.orderByDesc("createTime");
        Page<Product> page = productService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(page);
    }
}
