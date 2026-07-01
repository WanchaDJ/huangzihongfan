package com.yupi.yuoj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yuoj.common.BaseResponse;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.common.ResultUtils;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.exception.ThrowUtils;
import com.yupi.yuoj.model.dto.product.ProductQueryRequest;
import com.yupi.yuoj.model.dto.productfavour.ProductFavourAddRequest;
import com.yupi.yuoj.model.entity.Product;
import com.yupi.yuoj.model.entity.User;
import com.yupi.yuoj.service.ProductFavourService;
import com.yupi.yuoj.service.UserService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product_favour")
public class ProductFavourController {

    @Resource
    private ProductFavourService productFavourService;

    @Resource
    private UserService userService;

    @PostMapping("/")
    public BaseResponse<Integer> doProductFavour(@RequestBody ProductFavourAddRequest requestBody,
            HttpServletRequest request) {
        if (requestBody == null || requestBody.getProductId() == null || requestBody.getProductId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        int result = productFavourService.doProductFavour(requestBody.getProductId(), loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/my/list/page")
    public BaseResponse<Page<Product>> listMyFavourProductByPage(@RequestBody ProductQueryRequest queryRequest,
            HttpServletRequest request) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long pageSize = queryRequest.getPageSize();
        ThrowUtils.throwIf(pageSize > 50, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        Page<Product> page = productFavourService.listMyFavourProductByPage(current, pageSize, loginUser);
        return ResultUtils.success(page);
    }
}
