package com.example.hunstagram.global.aws.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.hunstagram.global.exception.CustomErrorCode;
import com.example.hunstagram.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import static com.example.hunstagram.global.exception.CustomErrorCode.IMAGE_UPLOAD_FAILED;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-15
 */
@RequiredArgsConstructor
@Service
public class AwsS3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadImage(MultipartFile file) {
        try {
            String fileType = "." + file.getContentType().split("/")[1];
            String randomNum = UUID.randomUUID().toString().substring(0, 6);
            String filename = System.currentTimeMillis() + randomNum + fileType;

            long size = file.getSize();

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(size);
            amazonS3.putObject(
                    new PutObjectRequest(bucket, filename, file.getInputStream(), objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );
            return amazonS3.getUrl(bucket, filename).toString();
        } catch (IOException e) {
            throw new CustomException(IMAGE_UPLOAD_FAILED, e);
        }
    }
}
