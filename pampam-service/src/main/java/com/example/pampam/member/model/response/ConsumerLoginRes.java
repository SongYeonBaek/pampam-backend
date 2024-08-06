package com.example.pampam.member.model.response;

import com.example.pampam.member.model.entity.Consumer;
import com.example.pampam.utils.JwtUtils;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsumerLoginRes {
    String token;

    public static ConsumerLoginRes buildConsumerLoginRes(Consumer consumer, String secretKey, Integer expiredTimeMs) {
        return ConsumerLoginRes.builder()
                .token(JwtUtils.generateAccessToken(consumer, secretKey, expiredTimeMs))
                .build();
    }
}
