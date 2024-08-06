package com.example.pampam.member.model.response;

import com.example.pampam.member.model.entity.Consumer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsumerUpdateRes {
    private Long consumerIdx;
    private String consumerPW;
    private String consumerName;
    private String consumerAddr;
    private String consumerPhoneNum;
    private String email;
    private String authority;
    private Boolean status;
    private Boolean socialLogin;

    public static ConsumerUpdateRes buildConsumerUpdateRes(Consumer consumer) {
        return ConsumerUpdateRes.builder()
                .consumerIdx(consumer.getConsumerIdx())
                .email(consumer.getEmail())
                .consumerPW(consumer.getConsumerPW())
                .consumerName(consumer.getConsumerName())
                .consumerAddr(consumer.getConsumerAddr())
                .consumerPhoneNum(consumer.getConsumerPhoneNum())
                .socialLogin(consumer.getSocialLogin())
                .authority(consumer.getAuthority())
                .status(consumer.getStatus())
                .build();
    }
}
