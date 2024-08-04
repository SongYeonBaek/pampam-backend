package com.example.pampam.member.model.response;

import com.example.pampam.member.model.entity.Consumer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsumerSignupRes {
    private Long consumerIdx;
    private String email;
    private String consumerName;
    private String consumerAddr;
    private String consumerPhoneNum;
    private String authority;
    private Boolean socialLogin;
    private Boolean status;

    public static ConsumerSignupRes buildConsumerSignupRes(Consumer consumer) {
        return ConsumerSignupRes.builder()
                .consumerIdx(consumer.getConsumerIdx())
                .email(consumer.getEmail())
                .consumerName(consumer.getConsumerName())
                .consumerAddr(consumer.getConsumerAddr())
                .authority(consumer.getAuthority())
                .socialLogin(consumer.getSocialLogin())
                .status(consumer.getStatus())
                .build();
    }
}
