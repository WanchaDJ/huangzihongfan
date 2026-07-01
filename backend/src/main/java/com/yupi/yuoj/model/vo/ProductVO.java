package com.yupi.yuoj.model.vo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yupi.yuoj.model.entity.Product;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class ProductVO implements Serializable {

    private final static Gson GSON = new Gson();

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer soldCount;
    private String coverImage;
    private List<String> images;
    private String type;
    private String category;
    private Integer status;
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public static Product voToObj(ProductVO productVO) {
        if (productVO == null) {
            return null;
        }
        Product product = new Product();
        BeanUtils.copyProperties(productVO, product);
        List<String> images = productVO.getImages();
        if (images != null) {
            product.setImages(GSON.toJson(images));
        }
        return product;
    }

    public static ProductVO objToVo(Product product) {
        if (product == null) {
            return null;
        }
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(product, productVO);
        productVO.setImages(GSON.fromJson(product.getImages(), new TypeToken<List<String>>() {
        }.getType()));
        return productVO;
    }
}
