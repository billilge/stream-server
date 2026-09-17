package kr.ac.kookmin.stream.client.file.local;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@ConfigurationProperties(prefix = "file.storage.local")
@Profile("!prod")
public record LocalFileStorageProperties(
    String basePath,
    String baseUrl,
    long uploadUrlExpirySeconds
) {}
