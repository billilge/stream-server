package kr.ac.kookmin.stream.client.file.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file.storage.s3")
public record S3FileStorageProperties(
    String bucket,
    String region,
    String endpoint,
    String accessKey,
    String secretKey,
    long uploadUrlExpirySeconds
) {}
