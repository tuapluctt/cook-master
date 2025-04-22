package vn.hvt.cook_master.service;


import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectAclRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import vn.hvt.cook_master.dto.ImageDTO;
import vn.hvt.cook_master.dto.request.PresignedUrlRequest;
import vn.hvt.cook_master.enums.AllowedFileType;
import vn.hvt.cook_master.exception.AppException;
import vn.hvt.cook_master.exception.ErrorCode;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StorageImageService {
    S3Client s3Client;
    S3Presigner presigner;

    @NonFinal
    @Value("${aws.s3.bucketName}")
    String bucketName;

    public ImageDTO uploadImage(MultipartFile file, String keyPrefix) {

        String fileName = generateUniqueFileName(file.getOriginalFilename());
        String key = keyPrefix + fileName;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));


            String url = getImageUrl(key);
            return ImageDTO.builder()
                    .url(url)
                    .imageKey(key)
                    .build();
        } catch (IOException e) {
            log.error("Error uploading file to S3: {}", e.getMessage());
            throw new AppException(ErrorCode.UPLOAD_IMAGE_FAILED);
        }
    }

    public ImageDTO generatePresignedUploadUrl(PresignedUrlRequest request, String keyPrefix ) {
        log.info("Generating presigned URL FileType: {}", AllowedFileType.valueOf(request.getFileType()).getType() );

        String fileName = generateUniqueFileName(request.getFileName());
        String key = keyPrefix + fileName;

        try {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(request.getFileType())
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .putObjectRequest(objectRequest)
                    .signatureDuration(Duration.ofMinutes(15)) // URL hết hạn sau 15 phút
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

            return ImageDTO.builder()
                    .presignedUrl(presignedRequest.url().toString())
                    .imageKey(key)
                    .build(); // Trả về URL để client PUT ảnh lên
        } catch (Exception e) {
            log.error("Error generating presigned URL: {}", e.getMessage());
            throw new AppException(ErrorCode.UPLOAD_IMAGE_FAILED);
        }




    }

    public String getImageUrl(String key) {
        return s3Client.utilities().getUrl(b -> b.bucket(bucketName).key(key)).toString();
    }

    public void deleteImage(String key) {
        s3Client.deleteObject(b -> b.bucket(bucketName).key(key));
    }


    private String generateUniqueFileName(String originalName) {
        return UUID.randomUUID() + "_" + originalName.replaceAll("\\s+", "_");
    }


    public String getUrl(String s3Key) {
        return s3Client.utilities().getUrl(b -> b.bucket(bucketName).key(s3Key)).toString();
    }
}
