package com.yupi.yuoj.model.vo;

import com.yupi.yuoj.model.entity.News;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class NewsVO implements Serializable {

    private Long id;
    private String title;
    private String summary;
    private String content;
    private String coverImage;
    private String category;
    private String source;
    private Date publishTime;
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public static News voToObj(NewsVO newsVO) {
        if (newsVO == null) {
            return null;
        }
        News news = new News();
        BeanUtils.copyProperties(newsVO, news);
        return news;
    }

    public static NewsVO objToVo(News news) {
        if (news == null) {
            return null;
        }
        NewsVO newsVO = new NewsVO();
        BeanUtils.copyProperties(news, newsVO);
        newsVO.setPublishTime(news.getPublishDate());
        return newsVO;
    }
}
