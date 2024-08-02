package com.example.pampam.member.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class GetProfileImageRes {
    private String profileImage;

    public static GetProfileImageRes buildProfileImageRes(String imageAddr) {
        return GetProfileImageRes.builder()
                .profileImage(imageAddr)
                .build();
    }
}
