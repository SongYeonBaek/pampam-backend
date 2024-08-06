package com.example.pampam.member.model.response;

import com.example.pampam.member.model.entity.Seller;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SellerUpdateRes {
    private Long sellerIdx;
    private String sellerPW;
    private String sellerName;
    private String sellerAddr;
    private String sellerPhoneNum;
    private String email;
    private String authority;
    private Boolean status;
    private String sellerBusinessNumber;

    public static SellerUpdateRes buildSellerUpdateRes(Seller seller) {
        return SellerUpdateRes.builder()
                .sellerIdx(seller.getSellerIdx())
                .email(seller.getEmail())
                .sellerPW(seller.getSellerPW())
                .sellerName(seller.getSellerName())
                .sellerAddr(seller.getSellerAddr())
                .sellerPhoneNum(seller.getSellerPhoneNum())
                .sellerBusinessNumber(seller.getSellerBusinessNumber())
                .authority(seller.getAuthority())
                .status(seller.getStatus())
                .build();
    }
}
