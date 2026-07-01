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
import com.yupi.yuoj.model.dto.news.NewsAddRequest;
import com.yupi.yuoj.model.dto.news.NewsQueryRequest;
import com.yupi.yuoj.model.entity.News;
import com.yupi.yuoj.service.NewsService;
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
 * 新闻接口
 */
@RestController
@RequestMapping("/news")
@Slf4j
public class NewsController {

    @Resource
    private NewsService newsService;

    @Resource
    private UserService userService;

    /**
     * 添加新闻（仅管理员）
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addNews(@RequestBody NewsAddRequest req) {
        if (req == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        News news = new News();
        BeanUtils.copyProperties(req, news);
        boolean result = newsService.save(news);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(news.getId());
    }

    /**
     * 删除新闻（仅管理员）
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteNews(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = newsService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新新闻（仅管理员）
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateNews(@RequestBody News news) {
        if (news == null || news.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = newsService.updateById(news);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取新闻
     */
    @GetMapping("/get/vo")
    public BaseResponse<News> getNewsById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        News news = newsService.getById(id);
        ThrowUtils.throwIf(news == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(news);
    }

    /**
     * 分页获取新闻列表
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<News>> listNewsByPage(@RequestBody NewsQueryRequest queryRequest) {
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<News> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(queryRequest.getSearchText())) {
            queryWrapper.like("title", queryRequest.getSearchText())
                    .or().like("content", queryRequest.getSearchText());
        }
        if (StringUtils.hasText(queryRequest.getTitle())) {
            queryWrapper.like("title", queryRequest.getTitle());
        }
        if (StringUtils.hasText(queryRequest.getCategory())) {
            queryWrapper.eq("category", queryRequest.getCategory());
        }
        if (queryRequest.getStatus() != null) {
            queryWrapper.eq("status", queryRequest.getStatus());
        }
        queryWrapper.orderByDesc("createTime");
        Page<News> page = newsService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(page);
    }
}
