package com.yupi.yuoj.model.vo;

import com.yupi.yuoj.model.entity.ChatMessage;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class ChatMessageVO implements Serializable {

    private Long id;
    private Long conversationId;
    private Long userId;
    private String type;
    private String content;
    private String files;
    private Date timestamp;

    private static final long serialVersionUID = 1L;

    public static ChatMessage voToObj(ChatMessageVO chatMessageVO) {
        if (chatMessageVO == null) {
            return null;
        }
        ChatMessage chatMessage = new ChatMessage();
        BeanUtils.copyProperties(chatMessageVO, chatMessage);
        return chatMessage;
    }

    public static ChatMessageVO objToVo(ChatMessage chatMessage) {
        if (chatMessage == null) {
            return null;
        }
        ChatMessageVO chatMessageVO = new ChatMessageVO();
        BeanUtils.copyProperties(chatMessage, chatMessageVO);
        return chatMessageVO;
    }
}
