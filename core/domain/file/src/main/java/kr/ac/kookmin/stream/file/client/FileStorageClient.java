package kr.ac.kookmin.stream.file.client;

import kr.ac.kookmin.stream.file.domain.UploadUrl;

public interface FileStorageClient {
    UploadUrl issuePresignedUrl(String fileKey, String contentType);
    void deleteObject(String fileKey);

    /**
     * 파일 키 맨 앞에 붙는 환경 폴더 이름(dev, prod 등). dev와 prod가 한 버킷을 같이 쓰므로 환경마다 폴더를 나눈다.
     */
    String getEnvironmentDirectory();
}
