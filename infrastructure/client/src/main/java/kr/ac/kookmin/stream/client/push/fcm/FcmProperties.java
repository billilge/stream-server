package kr.ac.kookmin.stream.client.push.fcm;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "push.fcm")
public record FcmProperties(
    String credentialsBase64
) {}
