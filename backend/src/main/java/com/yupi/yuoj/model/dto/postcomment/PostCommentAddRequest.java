package com.yupi.yuoj.model.dto.postcomment;

import java.io.Serializable;
import lombok.Data;

/**
 * 帖子评论添加请求
 *
 */
@Data
public class PostCommentAddRequest implements Serializable {

    /**
     * 帖子ID
     */
    private Long postId;

    /**
     * 内容
     */
    private String content;

    /**
     * 回复目标评论ID
     */
    private Long replyTo;

    private static final long serialVersionUID = 1L;
}
