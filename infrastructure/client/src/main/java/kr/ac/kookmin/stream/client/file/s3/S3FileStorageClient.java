package kr.ac.kookmin.stream.client.file.s3;

import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import kr.ac.kookmin.stream.internal.domain.file.client.FileStorageClient;
import kr.ac.kookmin.stream.internal.domain.file.domain.UploadUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

/**
 * S3 호환 스토리지(Cloudflare R2) 기반 구현체. presigned URL은 클라이언트가 스토리지에 직접 PUT하는 용도라,
 * 로컬 구현체와 달리 서버가 파일 바이트를 직접 받는 write(...)는 지원하지 않는다.
 * R2 엔드포인트·자격증명 설정은 {@link S3StorageConfig}가 만드는 {@link S3Client}/{@link S3Presigner} 빈에 있다.
 */
@Component
@ConditionalOnProperty(prefix = "file.storage", name = "type", havingValue = "s3")
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
    public void write(String fileKey, InputStream content) {
        throw new UnsupportedOperationException("S3는 클라이언트가 presigned URL로 직접 업로드하므로 서버가 파일을 받지 않는다");
    }

    @Override
    public void deleteObject(String fileKey) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
            .bucket(properties.bucket())
            .key(fileKey)
            .build());
    }
}
