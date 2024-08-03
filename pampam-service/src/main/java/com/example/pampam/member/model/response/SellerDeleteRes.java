package com.example.pampam.member.model.response;

import com.example.pampam.member.model.request.SellerDeleteReq;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SellerDeleteRes {
    private String email;

    public static SellerDeleteRes buildSellerDeleteRes(SellerDeleteReq sellerDeleteReq) {
        return SellerDeleteRes.builder()
                .email(sellerDeleteReq.getEmail())
                .build();
    }
}
