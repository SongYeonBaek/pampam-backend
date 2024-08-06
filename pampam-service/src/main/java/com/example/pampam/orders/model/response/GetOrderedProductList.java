package com.example.pampam.orders.model.response;

import com.example.pampam.product.model.entity.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class GetOrderedProductList {
    private Long idx;
    private String productName;
    private Integer salePrice;
    private List<String> filename;
    private Integer people;
    private Integer peopleCount;
    private Integer status;
    private Date closeAt;

    public static GetOrderedProductList entityToDto(Product product, List<String> filename, Integer status) {
        return GetOrderedProductList.builder()
                .idx(product.getIdx())
                .productName(product.getProductName())
                .salePrice(product.getSalePrice())
                .filename(filename)
                .people(product.getPeople())
                .peopleCount(product.getPeopleCount())
                .status(status)
                .closeAt(product.getCloseAt())
                .build();
    }
}
