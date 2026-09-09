package kr.ac.kookmin.stream.client.file.local;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import kr.ac.kookmin.stream.internal.domain.file.domain.UploadUrl;
import kr.ac.kookmin.stream.internal.domain.file.client.FileStorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 로컬 디스크 기반 임시 구현체. S3 연동 시 이 클래스와 "임시 로컬 업로드 엔드포인트"를 함께 제거한다.
 */
@Component
@RequiredArgsConstructor
public class LocalFileStorageClient implements FileStorageClient {

    private static final String LOCAL_UPLOAD_PATH = "/v1/admin/files/local-upload/";

    private final LocalFileStorageProperties properties;

    @Override
    public UploadUrl issuePresignedUrl(String fileKey, String contentType) {
        String url = properties.baseUrl() + LOCAL_UPLOAD_PATH + fileKey;
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(properties.uploadUrlExpirySeconds());
        return new UploadUrl(url, expiresAt);
    }

    @Override
    public void write(String fileKey, InputStream content) {
        try {
            Path path = resolvePath(fileKey);
            Files.createDirectories(path.getParent());
            Files.copy(content, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void deleteObject(String fileKey) {
        try {
            Files.deleteIfExists(resolvePath(fileKey));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Path resolvePath(String fileKey) {
        return Path.of(properties.basePath()).resolve(fileKey).normalize();
    }
}
