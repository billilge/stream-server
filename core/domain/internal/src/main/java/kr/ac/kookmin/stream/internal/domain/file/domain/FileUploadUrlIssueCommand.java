package kr.ac.kookmin.stream.internal.domain.file.domain;

public record FileUploadUrlIssueCommand(
    String originalName,
    String contentType,
    long fileSize,
    FileCategory category,
    Long uploaderId
) {}
