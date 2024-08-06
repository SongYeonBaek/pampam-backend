package com.example.pampam.member.model.response;

import com.example.pampam.member.model.entity.Seller;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class SellerSignupRes {
    private Long sellerIdx;
    private String email;
    private String sellerPW;
    private String sellerName;
    private String sellerAddr;
    private String sellerPhoneNum;
    private String authority;
    private Boolean status;
    private String sellerBusinessNumber;
    private String image;

    public static SellerSignupRes buildSellerSignupRes(Seller seller) {
        return SellerSignupRes.builder()
                .sellerIdx(seller.getSellerIdx())
                .email(seller.getEmail())
                .sellerPW(seller.getSellerPW())
                .sellerName(seller.getSellerName())
                .sellerAddr(seller.getSellerAddr())
                .sellerPhoneNum(seller.getSellerPhoneNum())
                .sellerPhoneNum(seller.getSellerPhoneNum())
                .authority(seller.getAuthority())
                .status(seller.getStatus())
                .sellerBusinessNumber(seller.getSellerBusinessNumber())
                .image(seller.getImage())
                .build();
    }

}
