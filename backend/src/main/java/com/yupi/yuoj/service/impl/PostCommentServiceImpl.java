package com.yupi.yuoj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yuoj.mapper.PostCommentMapper;
import com.yupi.yuoj.model.entity.PostComment;
import com.yupi.yuoj.service.PostCommentService;
import org.springframework.stereotype.Service;

@Service
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment> implements PostCommentService {
}
