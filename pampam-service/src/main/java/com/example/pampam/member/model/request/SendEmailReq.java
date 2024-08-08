package com.example.pampam.member.model.request;

import com.example.pampam.member.model.entity.Consumer;
import com.example.pampam.member.model.entity.Seller;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendEmailReq {
    private String email;
    private String authority;
    private String accessToken;

    public static SendEmailReq buildSendEmailReq(Consumer consumer, String accessToken) {
        return SendEmailReq.builder()
                .email(consumer.getEmail())
                .authority(consumer.getAuthority())
                .accessToken(accessToken)
                .build();
    }

    public static SendEmailReq buildSendEmailReq(Seller seller, String accessToken) {
        return SendEmailReq.builder()
                .email(seller.getEmail())
                .authority(seller.getAuthority())
                .accessToken(accessToken)
                .build();
    }

    public static SendEmailReq buildSendEmailReq(String email) {
        return SendEmailReq.builder()
                .email(email)
                .build();
    }

}
