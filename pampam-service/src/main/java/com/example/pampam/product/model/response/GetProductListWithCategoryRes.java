package com.example.pampam.product.model.response;

import com.example.pampam.product.model.entity.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Builder
@Setter
@Getter
public class GetProductListWithCategoryRes {
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

    public static GetProductListWithCategoryRes entityToDto(Product product, List<String> filename) {
        return GetProductListWithCategoryRes.builder()
                .idx(product.getIdx())
                .productName(product.getProductName())
                .price(product.getPrice())
                .salePrice(product.getSalePrice())
                .productInfo(product.getProductInfo())
                .filename(filename)
                .sellerIdx(product.getSellerIdx())
                .peopleCount(product.getPeopleCount())
                .startAt(product.getStartAt())
                .closeAt(product.getCloseAt())
                .build();
    }
}
