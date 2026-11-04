package kr.ac.kookmin.stream.client.file.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @param environmentDirectory 파일 키 맨 앞에 붙는 환경 폴더. 활성 프로파일 이름을 그대로 쓴다
 */
@ConfigurationProperties(prefix = "file.storage.s3")
public record S3FileStorageProperties(
    String bucket,
    String region,
    String endpoint,
    String accessKey,
    String secretKey,
    long uploadUrlExpirySeconds,
    String environmentDirectory
) {}
