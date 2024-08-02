package com.example.pampam.member.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ProfileImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String imagePath;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consumer_idx")
    private Consumer consumer;

    public static ProfileImage buildProfileImage(String imageAddr, Consumer consumer) {
        return ProfileImage.builder()
                .imagePath(imageAddr)
                .consumer(consumer)
                .build();
    }
}
