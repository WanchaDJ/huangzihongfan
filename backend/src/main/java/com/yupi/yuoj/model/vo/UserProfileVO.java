package com.yupi.yuoj.model.vo;

import com.yupi.yuoj.model.entity.UserProfile;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class UserProfileVO implements Serializable {

    private Long id;
    private Long userId;
    private String memberLevel;
    private Integer growthValue;
    private BigDecimal totalSpend;
    private Integer points;
    private String inviteCode;
    private Integer inviteCount;
    private Date memberExpireTime;
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public static UserProfile voToObj(UserProfileVO userProfileVO) {
        if (userProfileVO == null) {
            return null;
        }
        UserProfile userProfile = new UserProfile();
        BeanUtils.copyProperties(userProfileVO, userProfile);
        return userProfile;
    }

    public static UserProfileVO objToVo(UserProfile userProfile) {
        if (userProfile == null) {
            return null;
        }
        UserProfileVO userProfileVO = new UserProfileVO();
        BeanUtils.copyProperties(userProfile, userProfileVO);
        return userProfileVO;
    }
}
