package kr.ac.kookmin.stream.internal.domain.file.client;

import java.io.InputStream;
import kr.ac.kookmin.stream.internal.domain.file.domain.UploadUrl;

public interface FileStorageClient {
    UploadUrl issuePresignedUrl(String fileKey, String contentType);
    void write(String fileKey, InputStream content);
    void deleteObject(String fileKey);
}
