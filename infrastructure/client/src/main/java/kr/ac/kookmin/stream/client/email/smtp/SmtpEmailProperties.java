package kr.ac.kookmin.stream.client.email.smtp;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "email.smtp")
public record SmtpEmailProperties(
    String from,
    String fromName
) {}
