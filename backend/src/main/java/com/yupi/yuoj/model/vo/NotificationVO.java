package com.yupi.yuoj.model.vo;

import com.yupi.yuoj.model.entity.Notification;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class NotificationVO implements Serializable {

    private Long id;
    private Long userId;
    private String content;
    private String type;
    private Long relatedId;
    private Boolean isRead;
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public static Notification voToObj(NotificationVO notificationVO) {
        if (notificationVO == null) {
            return null;
        }
        Notification notification = new Notification();
        BeanUtils.copyProperties(notificationVO, notification);
        if (notificationVO.getIsRead() != null) {
            notification.setIsRead(notificationVO.getIsRead() ? 1 : 0);
        }
        return notification;
    }

    public static NotificationVO objToVo(Notification notification) {
        if (notification == null) {
            return null;
        }
        NotificationVO notificationVO = new NotificationVO();
        BeanUtils.copyProperties(notification, notificationVO);
        if (notification.getIsRead() != null) {
            notificationVO.setIsRead(notification.getIsRead() == 1);
        }
        return notificationVO;
    }
}
