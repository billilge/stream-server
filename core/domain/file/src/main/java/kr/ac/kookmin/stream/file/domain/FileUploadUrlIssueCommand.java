package kr.ac.kookmin.stream.file.domain;

public record FileUploadUrlIssueCommand(
    String originalName,
    String contentType,
    long fileSize,
    FileCategory category,
    Long uploaderId
) {}
