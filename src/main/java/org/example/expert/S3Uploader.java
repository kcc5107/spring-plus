package org.example.expert;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class S3Uploader {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.path.profileImage}")
    private String path;

    public String upload(MultipartFile multipartFile) throws IOException {
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
        return uploadFile(uploadFile, path);
    }

    private String uploadFile(File uploadFile, String path) {
        String fileName = path + "/" + UUID.randomUUID() + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);

        removeNewFile(uploadFile);  // 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)

        return uploadImageUrl;      // 업로드된 파일의 S3 URL 주소 반환
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile)
        );
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일 삭제가 완료되었습니다.");
        } else {
            log.info("파일 삭제가 실패되었습니다.");
        }
    }

    private Optional<File> convert(MultipartFile files) throws IOException {
        String uuid = UUID.randomUUID().toString();
        String filename = uuid + "_" + files.getOriginalFilename();  // 고유 이름 생성

        File convertFile = new File(System.getProperty("java.io.tmpdir") + File.separator + filename);

        log.info("파일명: {}", files.getOriginalFilename());
        log.info("경로: {}", convertFile.getAbsolutePath());
        log.info("파일 생성 가능 여부: {}", convertFile.canWrite());
        log.info("임시파일 존재 여부: {}", convertFile.exists());

        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(files.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    //file 삭제
    public void fileDelete(String s3Key) {
        try {
            amazonS3.deleteObject(this.bucket, s3Key);
        } catch (AmazonServiceException e) {
            System.out.println(e.getErrorMessage());
        }
    }
}
