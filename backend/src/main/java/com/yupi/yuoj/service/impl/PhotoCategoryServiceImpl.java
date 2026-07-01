package com.yupi.yuoj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yuoj.mapper.PhotoCategoryMapper;
import com.yupi.yuoj.model.entity.PhotoCategory;
import com.yupi.yuoj.service.PhotoCategoryService;
import org.springframework.stereotype.Service;

@Service
public class PhotoCategoryServiceImpl extends ServiceImpl<PhotoCategoryMapper, PhotoCategory> implements PhotoCategoryService {
}
