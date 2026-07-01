package com.yupi.yuoj.model.vo;

import com.yupi.yuoj.model.entity.ContactMessage;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class ContactMessageVO implements Serializable {

    private Long id;
    private String name;
    private String email;
    private String subject;
    private String message;
    private Boolean isRead;
    private String reply;
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public static ContactMessage voToObj(ContactMessageVO contactMessageVO) {
        if (contactMessageVO == null) {
            return null;
        }
        ContactMessage contactMessage = new ContactMessage();
        BeanUtils.copyProperties(contactMessageVO, contactMessage);
        if (contactMessageVO.getIsRead() != null) {
            contactMessage.setIsRead(contactMessageVO.getIsRead() ? 1 : 0);
        }
        return contactMessage;
    }

    public static ContactMessageVO objToVo(ContactMessage contactMessage) {
        if (contactMessage == null) {
            return null;
        }
        ContactMessageVO contactMessageVO = new ContactMessageVO();
        BeanUtils.copyProperties(contactMessage, contactMessageVO);
        if (contactMessage.getIsRead() != null) {
            contactMessageVO.setIsRead(contactMessage.getIsRead() == 1);
        }
        return contactMessageVO;
    }
}
