package kr.ac.kookmin.stream.file.client;

import kr.ac.kookmin.stream.file.domain.UploadUrl;

public interface FileStorageClient {
    UploadUrl issuePresignedUrl(String fileKey, String contentType);
    void deleteObject(String fileKey);
}
