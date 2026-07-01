package com.yupi.yuoj.model.vo;

import com.yupi.yuoj.model.entity.Photo;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class PhotoVO implements Serializable {

    private Long id;
    private Long categoryId;
    private String title;
    private String description;
    private String imageUrl;
    private String thumbnailUrl;
    private Integer sortOrder;
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public static Photo voToObj(PhotoVO photoVO) {
        if (photoVO == null) {
            return null;
        }
        Photo photo = new Photo();
        BeanUtils.copyProperties(photoVO, photo);
        return photo;
    }

    public static PhotoVO objToVo(Photo photo) {
        if (photo == null) {
            return null;
        }
        PhotoVO photoVO = new PhotoVO();
        BeanUtils.copyProperties(photo, photoVO);
        return photoVO;
    }
}
