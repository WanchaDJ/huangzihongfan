package com.yupi.yuoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.mapper.ProductFavourMapper;
import com.yupi.yuoj.model.entity.Product;
import com.yupi.yuoj.model.entity.ProductFavour;
import com.yupi.yuoj.model.entity.User;
import com.yupi.yuoj.service.ProductFavourService;
import com.yupi.yuoj.service.ProductService;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductFavourServiceImpl extends ServiceImpl<ProductFavourMapper, ProductFavour>
        implements ProductFavourService {

    @Resource
    private ProductService productService;

    @Override
    public int doProductFavour(long productId, User loginUser) {
        Product product = productService.getById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商品不存在");
        }
        long userId = loginUser.getId();
        ProductFavourService productFavourService = (ProductFavourService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            return productFavourService.doProductFavourInner(userId, productId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doProductFavourInner(long userId, long productId) {
        QueryWrapper<ProductFavour> queryWrapper = new QueryWrapper<ProductFavour>()
                .eq("userId", userId)
                .eq("productId", productId);
        ProductFavour oldProductFavour = this.getOne(queryWrapper);
        boolean result;
        if (oldProductFavour != null) {
            result = this.remove(queryWrapper);
            if (!result) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
            return -1;
        }
        ProductFavour productFavour = new ProductFavour();
        productFavour.setUserId(userId);
        productFavour.setProductId(productId);
        result = this.save(productFavour);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return 1;
    }

    @Override
    public Page<Product> listMyFavourProductByPage(long current, long pageSize, User loginUser) {
        Page<ProductFavour> favourPage = this.page(
                new Page<>(current, pageSize),
                new QueryWrapper<ProductFavour>()
                        .eq("userId", loginUser.getId())
                        .orderByDesc("createTime"));
        List<Long> productIds = favourPage.getRecords().stream()
                .map(ProductFavour::getProductId)
                .collect(Collectors.toList());
        if (productIds.isEmpty()) {
            return new Page<>(current, pageSize, 0);
        }
        List<Product> productList = productService.listByIds(productIds);
        Map<Long, Product> productMap = productList.stream()
                .collect(Collectors.toMap(Product::getId, item -> item, (left, right) -> left, LinkedHashMap::new));
        List<Product> orderedProducts = productIds.stream()
                .map(productMap::get)
                .filter(item -> item != null)
                .collect(Collectors.toList());
        Page<Product> productPage = new Page<>(current, pageSize, favourPage.getTotal());
        productPage.setRecords(orderedProducts.isEmpty() ? Collections.emptyList() : orderedProducts);
        return productPage;
    }
}
