package com.yupi.yuoj.model.dto.productfavour;

import java.io.Serializable;
import lombok.Data;

@Data
public class ProductFavourAddRequest implements Serializable {

    private Long productId;

    private static final long serialVersionUID = 1L;
}
