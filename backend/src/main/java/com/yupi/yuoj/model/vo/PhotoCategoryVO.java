package com.yupi.yuoj.model.vo;

import com.yupi.yuoj.model.entity.PhotoCategory;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class PhotoCategoryVO implements Serializable {

    private Long id;
    private String name;
    private String description;
    private String coverImage;
    private Integer sortOrder;
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public static PhotoCategory voToObj(PhotoCategoryVO photoCategoryVO) {
        if (photoCategoryVO == null) {
            return null;
        }
        PhotoCategory photoCategory = new PhotoCategory();
        BeanUtils.copyProperties(photoCategoryVO, photoCategory);
        return photoCategory;
    }

    public static PhotoCategoryVO objToVo(PhotoCategory photoCategory) {
        if (photoCategory == null) {
            return null;
        }
        PhotoCategoryVO photoCategoryVO = new PhotoCategoryVO();
        BeanUtils.copyProperties(photoCategory, photoCategoryVO);
        return photoCategoryVO;
    }
}
