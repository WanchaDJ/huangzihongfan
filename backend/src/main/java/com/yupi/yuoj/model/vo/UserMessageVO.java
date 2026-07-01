package com.yupi.yuoj.model.vo;

import com.yupi.yuoj.model.entity.UserMessage;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class UserMessageVO implements Serializable {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private Boolean isRead;
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public static UserMessage voToObj(UserMessageVO userMessageVO) {
        if (userMessageVO == null) {
            return null;
        }
        UserMessage userMessage = new UserMessage();
        BeanUtils.copyProperties(userMessageVO, userMessage);
        if (userMessageVO.getIsRead() != null) {
            userMessage.setIsRead(userMessageVO.getIsRead() ? 1 : 0);
        }
        return userMessage;
    }

    public static UserMessageVO objToVo(UserMessage userMessage) {
        if (userMessage == null) {
            return null;
        }
        UserMessageVO userMessageVO = new UserMessageVO();
        BeanUtils.copyProperties(userMessage, userMessageVO);
        if (userMessage.getIsRead() != null) {
            userMessageVO.setIsRead(userMessage.getIsRead() == 1);
        }
        return userMessageVO;
    }
}
