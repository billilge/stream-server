package kr.ac.kookmin.stream.client.file.s3;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import kr.ac.kookmin.stream.file.client.FileStorageClient;
import kr.ac.kookmin.stream.file.domain.UploadUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

/**
 * S3 호환 스토리지(Cloudflare R2) 기반 구현체.
 * R2 엔드포인트·자격증명 설정은 {@link S3StorageConfig}가 만드는 {@link S3Client}/{@link S3Presigner} 빈에 있다.
 */
@Component
@RequiredArgsConstructor
public class S3FileStorageClient implements FileStorageClient {

    private final S3FileStorageProperties properties;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Override
    public UploadUrl issuePresignedUrl(String fileKey, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(properties.bucket())
            .key(fileKey)
            .contentType(contentType)
            .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofSeconds(properties.uploadUrlExpirySeconds()))
            .putObjectRequest(putObjectRequest)
            .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        LocalDateTime expiresAt = LocalDateTime.ofInstant(presignedRequest.expiration(), ZoneId.systemDefault());
        return new UploadUrl(presignedRequest.url().toString(), expiresAt);
    }

    @Override
    public void deleteObject(String fileKey) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
            .bucket(properties.bucket())
            .key(fileKey)
            .build());
    }

    @Override
    public String publicBaseUrl() {
        return properties.publicBaseUrl();
    }
}
