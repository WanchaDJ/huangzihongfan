package com.yupi.yuoj.model.vo;

import com.yupi.yuoj.model.entity.PostComment;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class PostCommentVO implements Serializable {

    private Long id;
    private Long postId;
    private Long userId;
    private String content;
    private Long replyTo;
    private Date createTime;

    private UserVO user;

    private static final long serialVersionUID = 1L;

    public static PostComment voToObj(PostCommentVO postCommentVO) {
        if (postCommentVO == null) {
            return null;
        }
        PostComment postComment = new PostComment();
        BeanUtils.copyProperties(postCommentVO, postComment);
        return postComment;
    }

    public static PostCommentVO objToVo(PostComment postComment) {
        if (postComment == null) {
            return null;
        }
        PostCommentVO postCommentVO = new PostCommentVO();
        BeanUtils.copyProperties(postComment, postCommentVO);
        return postCommentVO;
    }
}
