package kr.ac.kookmin.stream.file.client;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.file.domain.FileUrl;
import kr.ac.kookmin.stream.file.domain.UploadUrl;

public interface FileStorageClient {
    UploadUrl issuePresignedUrl(String fileKey, String contentType);
    void write(String fileKey, InputStream content);
    void deleteObject(String fileKey);
    FileUrl publicUrl(String fileKey);
    Map<String, FileUrl> publicUrls(List<String> fileKeys);

    /** publicBaseUrl 끝에 슬래시가 있어도 이어붙인 URL에 슬래시가 중복되지 않게 한다. */
    static String buildPublicUrl(String publicBaseUrl, String fileKey) {
        String trimmedBaseUrl = publicBaseUrl.endsWith("/")
            ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1)
            : publicBaseUrl;
        return trimmedBaseUrl + "/" + fileKey;
    }
}
