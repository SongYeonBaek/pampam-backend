package com.example.pampam.member.model.response;

import com.example.pampam.member.model.entity.Seller;
import com.example.pampam.member.model.request.SellerSignupReq;
import com.example.pampam.utils.JwtUtils;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SellerLoginRes {
    String token;

    public static SellerLoginRes buildSellerLoginRes(Seller seller, String secretKey, Integer expiredTimeMs) {
        return SellerLoginRes.builder()
                .token(JwtUtils.generateAccessToken(seller, secretKey, expiredTimeMs))
                .build();
    }
}
