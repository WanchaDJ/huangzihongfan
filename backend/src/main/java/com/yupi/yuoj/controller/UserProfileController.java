package com.yupi.yuoj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.yuoj.common.BaseResponse;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.common.ResultUtils;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.exception.ThrowUtils;
import com.yupi.yuoj.model.dto.userprofile.UserProfileUpdateRequest;
import com.yupi.yuoj.model.entity.User;
import com.yupi.yuoj.model.entity.UserProfile;
import com.yupi.yuoj.service.UserProfileService;
import com.yupi.yuoj.service.UserService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/profile")
@Slf4j
public class UserProfileController {

    @Resource
    private UserProfileService userProfileService;

    @Resource
    private UserService userService;

    @PostMapping("/update")
    public BaseResponse<Boolean> updateProfile(@RequestBody UserProfileUpdateRequest req, HttpServletRequest request) {
        if (req == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        UserProfile profile = getOrCreateProfile(loginUser.getId());
        applyUpdate(req, profile);
        profile.setUpdateTime(new Date());
        boolean result = userProfileService.saveOrUpdate(profile);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    public BaseResponse<UserProfile> getProfile(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(getOrCreateProfile(loginUser.getId()));
    }

    private UserProfile getOrCreateProfile(Long userId) {
        UserProfile profile = userProfileService.getOne(new QueryWrapper<UserProfile>().eq("userId", userId).last("limit 1"));
        if (profile == null) {
            Date now = new Date();
            profile = new UserProfile();
            profile.setUserId(userId);
            profile.setMemberLevel("regular");
            profile.setGrowthValue(0);
            profile.setPoints(0);
            profile.setTotalSpend(BigDecimal.ZERO);
            profile.setInviteCode("HZHF" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase());
            profile.setInviteCount(0);
            profile.setCreateTime(now);
            profile.setUpdateTime(now);
            boolean saved = userProfileService.save(profile);
            ThrowUtils.throwIf(!saved, ErrorCode.OPERATION_ERROR);
        } else {
            boolean dirty = false;
            if (profile.getMemberLevel() == null) {
                profile.setMemberLevel("regular");
                dirty = true;
            }
            if (profile.getGrowthValue() == null) {
                profile.setGrowthValue(0);
                dirty = true;
            }
            if (profile.getPoints() == null) {
                profile.setPoints(0);
                dirty = true;
            }
            if (profile.getTotalSpend() == null) {
                profile.setTotalSpend(BigDecimal.ZERO);
                dirty = true;
            }
            if (profile.getInviteCode() == null) {
                profile.setInviteCode("HZHF" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase());
                dirty = true;
            }
            if (profile.getInviteCount() == null) {
                profile.setInviteCount(0);
                dirty = true;
            }
            if (dirty) {
                profile.setUpdateTime(new Date());
                userProfileService.updateById(profile);
            }
        }
        return profile;
    }

    private void applyUpdate(UserProfileUpdateRequest req, UserProfile profile) {
        if (req.getPhone() != null) {
            profile.setPhone(req.getPhone());
        }
        if (req.getBirthday() != null) {
            profile.setBirthday(req.getBirthday());
        }
        if (req.getProvince() != null) {
            profile.setProvince(req.getProvince());
        }
        if (req.getCity() != null) {
            profile.setCity(req.getCity());
        }
        if (req.getDistrict() != null) {
            profile.setDistrict(req.getDistrict());
        }
        if (req.getDetailAddress() != null) {
            profile.setDetailAddress(req.getDetailAddress());
        }
        if (req.getBio() != null) {
            profile.setBio(req.getBio());
        }
        if (req.getGender() != null) {
            profile.setGender(req.getGender());
        }
        if (req.getMemberLevel() != null) {
            profile.setMemberLevel(req.getMemberLevel());
        }
        if (req.getMemberExpireTime() != null) {
            profile.setMemberExpireTime(req.getMemberExpireTime());
        }
        if (req.getGrowthValue() != null) {
            profile.setGrowthValue(req.getGrowthValue());
        }
        if (req.getPoints() != null) {
            profile.setPoints(req.getPoints());
        }
        if (req.getTotalSpend() != null) {
            profile.setTotalSpend(req.getTotalSpend());
        }
        if (req.getInviteCode() != null) {
            profile.setInviteCode(req.getInviteCode());
        }
        if (req.getInviteCount() != null) {
            profile.setInviteCount(req.getInviteCount());
        }
    }
}
