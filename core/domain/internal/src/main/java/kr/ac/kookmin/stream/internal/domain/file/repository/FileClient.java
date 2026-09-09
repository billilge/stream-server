package kr.ac.kookmin.stream.internal.domain.file.repository;

import java.io.InputStream;
import kr.ac.kookmin.stream.internal.domain.file.domain.UploadUrl;

public interface FileClient {
    UploadUrl issueUploadUrl(String fileKey, String contentType);
    void write(String fileKey, InputStream content);
    void deleteObject(String fileKey);
}
