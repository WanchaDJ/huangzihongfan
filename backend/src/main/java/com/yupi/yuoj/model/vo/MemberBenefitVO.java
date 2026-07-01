package com.yupi.yuoj.model.vo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yupi.yuoj.model.entity.MemberBenefit;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class MemberBenefitVO implements Serializable {

    private final static Gson GSON = new Gson();

    private Long id;
    private String name;
    private String description;
    private String icon;
    private List<String> levels;
    private Integer sortOrder;
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public static MemberBenefit voToObj(MemberBenefitVO memberBenefitVO) {
        if (memberBenefitVO == null) {
            return null;
        }
        MemberBenefit memberBenefit = new MemberBenefit();
        BeanUtils.copyProperties(memberBenefitVO, memberBenefit);
        List<String> levels = memberBenefitVO.getLevels();
        if (levels != null) {
            memberBenefit.setLevels(GSON.toJson(levels));
        }
        return memberBenefit;
    }

    public static MemberBenefitVO objToVo(MemberBenefit memberBenefit) {
        if (memberBenefit == null) {
            return null;
        }
        MemberBenefitVO memberBenefitVO = new MemberBenefitVO();
        BeanUtils.copyProperties(memberBenefit, memberBenefitVO);
        memberBenefitVO.setLevels(GSON.fromJson(memberBenefit.getLevels(), new TypeToken<List<String>>() {
        }.getType()));
        return memberBenefitVO;
    }
}
