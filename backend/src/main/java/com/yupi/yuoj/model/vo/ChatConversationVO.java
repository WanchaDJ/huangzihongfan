package com.yupi.yuoj.model.vo;

import com.yupi.yuoj.model.entity.ChatConversation;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class ChatConversationVO implements Serializable {

    private Long id;
    private Long userId;
    private String title;
    private String context;
    private Date createdAt;
    private Date updatedAt;

    private static final long serialVersionUID = 1L;

    public static ChatConversation voToObj(ChatConversationVO chatConversationVO) {
        if (chatConversationVO == null) {
            return null;
        }
        ChatConversation chatConversation = new ChatConversation();
        BeanUtils.copyProperties(chatConversationVO, chatConversation);
        return chatConversation;
    }

    public static ChatConversationVO objToVo(ChatConversation chatConversation) {
        if (chatConversation == null) {
            return null;
        }
        ChatConversationVO chatConversationVO = new ChatConversationVO();
        BeanUtils.copyProperties(chatConversation, chatConversationVO);
        return chatConversationVO;
    }
}
