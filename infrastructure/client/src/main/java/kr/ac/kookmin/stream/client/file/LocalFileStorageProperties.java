package kr.ac.kookmin.stream.client.file;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file.storage.local")
public record LocalFileStorageProperties(
    String basePath,
    String baseUrl,
    long uploadUrlExpirySeconds
) {}
