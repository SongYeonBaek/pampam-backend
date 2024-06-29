package com.example.pampam.category.model.response;

import com.example.pampam.product.model.entity.Product;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Builder
@Getter
public class GetSearchProductToCategory {
    private Long idx;
    private String productName;
    private Integer price;
    private Integer salePrice;
    private String productInfo;
    private List<String> filename;
    private Long sellerIdx ;
    private Integer peopleCount;
    private Date startAt;
    private Date closeAt;

    public static GetSearchProductToCategory buildProductToCategory(Product product, List<String> productImages) {
        return GetSearchProductToCategory.builder()
                .idx(product.getIdx())
                .productName(product.getProductName())
                .price(product.getPrice())
                .salePrice(product.getSalePrice())
                .productInfo(product.getProductInfo())
                .filename(productImages)
                .sellerIdx(product.getSellerIdx())
                .peopleCount(product.getPeopleCount())
                .startAt(product.getStartAt())
                .closeAt(product.getCloseAt())
                .build();
    }
}
