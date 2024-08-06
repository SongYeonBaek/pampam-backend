package com.example.pampam.member.model.response;

import com.example.pampam.member.model.request.ConsumerDeleteReq;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsumerDeleteRes {
    private String email;

    public static ConsumerDeleteRes buildConsumerDeleteRes(ConsumerDeleteReq consumerDeleteReq) {
        return ConsumerDeleteRes.builder()
                .email(consumerDeleteReq.getEmail())
                .build();
    }
}
