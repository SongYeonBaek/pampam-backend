package com.example.pampam.member.repository;

import com.example.pampam.member.model.entity.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
    Optional<ProfileImage> findByConsumerEmail(String email);
}
