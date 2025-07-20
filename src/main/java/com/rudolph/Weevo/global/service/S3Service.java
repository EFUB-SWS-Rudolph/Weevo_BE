package com.rudolph.Weevo.global.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    // application.yml 에서 주입받은 리전
    @Value("${aws.s3.region}")
    private String region;

    /**
     * 파일을 지정한 디렉토리(dir)에 업로드하고,
     * 업로드된 객체의 퍼블릭 URL을 반환합니다.
     *
     * @param file MultipartFile
     * @param dir  ex) "courses/123"
     * @return public URL
     */
    public String uploadFile(MultipartFile file, String dir) {
        // 확장자 추출
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        // 키 생성 (UUID 기반)
        String key = String.format("%s/%s.%s", dir, UUID.randomUUID(), ext);

        try (InputStream is = file.getInputStream()) {
            PutObjectRequest por = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(por, RequestBody.fromInputStream(is, file.getSize()));
        } catch (IOException e) {
            throw new RuntimeException("S3 파일 업로드 실패", e);
        }

        // 주입받은 region 값을 사용해 URL 생성
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucket, region, key);
    }
}
