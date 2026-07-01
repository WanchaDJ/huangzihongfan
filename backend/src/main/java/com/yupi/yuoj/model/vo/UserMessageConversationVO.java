package com.yupi.yuoj.model.vo;

import com.yupi.yuoj.model.entity.UserMessage;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class UserMessageConversationVO implements Serializable {

    private Long otherUserId;

    private UserVO otherUser;

    private UserMessage lastMessage;

    private Integer unreadCount;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
