package com.yupi.yuoj.model.dto.userprofile;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class UserProfileUpdateRequest implements Serializable {

    private String phone;

    private Date birthday;

    private String province;

    private String city;

    private String district;

    private String detailAddress;

    private String bio;

    private Integer gender;

    private String memberLevel;

    private Date memberExpireTime;

    private Integer growthValue;

    private Integer points;

    private BigDecimal totalSpend;

    private String inviteCode;

    private Integer inviteCount;

    private static final long serialVersionUID = 1L;
}
