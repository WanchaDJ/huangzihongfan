package com.yupi.yuoj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@TableName(value = "user_profile")
@Data
public class UserProfile implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
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
    private Date createTime;
    private Date updateTime;
    @TableLogic
    private Integer isDelete;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
