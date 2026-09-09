package kr.ac.kookmin.stream.internal.domain.file.domain;

import java.time.LocalDateTime;

public record FileUploadUrlIssueResult(
    Long fileId,
    String uploadUrl,
    String fileKey,
    LocalDateTime expiresAt
) {}
