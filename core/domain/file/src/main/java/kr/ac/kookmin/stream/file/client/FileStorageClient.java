package kr.ac.kookmin.stream.file.client;

import java.io.InputStream;
import kr.ac.kookmin.stream.file.domain.UploadUrl;

public interface FileStorageClient {
    UploadUrl issuePresignedUrl(String fileKey, String contentType);
    void write(String fileKey, InputStream content);
    void deleteObject(String fileKey);
    String publicBaseUrl();
}
