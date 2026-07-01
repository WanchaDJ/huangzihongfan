package com.yupi.yuoj.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yuoj.model.entity.Product;
import com.yupi.yuoj.model.entity.ProductFavour;
import com.yupi.yuoj.model.entity.User;

public interface ProductFavourService extends IService<ProductFavour> {

    int doProductFavour(long productId, User loginUser);

    int doProductFavourInner(long userId, long productId);

    Page<Product> listMyFavourProductByPage(long current, long pageSize, User loginUser);
}
