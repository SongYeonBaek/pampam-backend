package com.example.pampam.member.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.pampam.member.model.entity.Consumer;
import com.example.pampam.member.model.entity.ProfileImage;
import com.example.pampam.member.repository.ProfileImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileImageService {

    private final ProfileImageRepository profileImageRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 s3;

    public String saveProfileImage(MultipartFile profileImage) {
        return uploadFile(profileImage);
    }

    private String makeFolder() {
        String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String folderPath = str.replace("/", File.separator);

        return folderPath;
    }
    private String uploadFile(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        String folderPath = makeFolder();
        String uuid = UUID.randomUUID().toString();
        String saveFileName = folderPath + File.separator + uuid + "_" + originalName;
        InputStream input = null;
        try {
            input = file.getInputStream();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());


            s3.putObject(bucket, saveFileName.replace(File.separator, "/"), input, metadata);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return s3.getUrl(bucket, saveFileName.replace(File.separator, "/")).toString();
    }

    public void saveProfileImage(MultipartFile profileImage, Consumer consumer) {
        String imageAddr = saveProfileImage(profileImage);
        ProfileImage profile = profileImageRepository.save(ProfileImage.buildProfileImage(imageAddr, consumer));
        saveProfileImage(profileImage);
    }

    public String findProfileImage(String email) {
        Optional<ProfileImage> profileImage = profileImageRepository.findByConsumerEmail(email);
        if (profileImage.isPresent()) {
            return profileImage.get().getImagePath();
        }
        return null;
    }
}
