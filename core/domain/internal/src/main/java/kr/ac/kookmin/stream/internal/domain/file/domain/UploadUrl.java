package kr.ac.kookmin.stream.internal.domain.file.domain;

import java.time.LocalDateTime;

public record UploadUrl(String url, LocalDateTime expiresAt) {}
