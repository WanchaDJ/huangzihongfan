package com.yupi.yuoj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yuoj.common.BaseResponse;
import com.yupi.yuoj.common.DeleteRequest;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.common.ResultUtils;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.exception.ThrowUtils;
import com.yupi.yuoj.model.dto.postcomment.PostCommentAddRequest;
import com.yupi.yuoj.model.dto.postcomment.PostCommentQueryRequest;
import com.yupi.yuoj.model.entity.PostComment;
import com.yupi.yuoj.model.entity.User;
import com.yupi.yuoj.service.PostCommentService;
import com.yupi.yuoj.service.UserService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 帖子评论接口
 */
@RestController
@RequestMapping("/post/comment")
@Slf4j
public class PostCommentController {

    @Resource
    private PostCommentService postCommentService;

    @Resource
    private UserService userService;

    /**
     * 添加评论
     */
    @PostMapping("/add")
    public BaseResponse<Long> addComment(@RequestBody PostCommentAddRequest req,
                                          HttpServletRequest request) {
        if (req == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        PostComment comment = new PostComment();
        BeanUtils.copyProperties(req, comment);
        comment.setUserId(loginUser.getId());
        boolean result = postCommentService.save(comment);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(comment.getId());
    }

    /**
     * 删除评论（本人或管理员）
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteComment(@RequestBody DeleteRequest deleteRequest,
                                                HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        PostComment oldComment = postCommentService.getById(id);
        ThrowUtils.throwIf(oldComment == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldComment.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = postCommentService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 分页获取评论列表（可按帖子ID筛选）
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<PostComment>> listCommentsByPage(
            @RequestBody PostCommentQueryRequest queryRequest) {
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<PostComment> queryWrapper = new QueryWrapper<>();
        if (queryRequest.getPostId() != null) {
            queryWrapper.eq("postId", queryRequest.getPostId());
        }
        if (queryRequest.getUserId() != null) {
            queryWrapper.eq("userId", queryRequest.getUserId());
        }
        queryWrapper.orderByDesc("createTime");
        Page<PostComment> page = postCommentService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(page);
    }
}
