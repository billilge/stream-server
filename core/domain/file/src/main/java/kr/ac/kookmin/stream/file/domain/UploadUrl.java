package kr.ac.kookmin.stream.file.domain;

import java.time.LocalDateTime;

public record UploadUrl(String url, LocalDateTime expiresAt) {}
