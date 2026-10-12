package kr.ac.kookmin.stream.file.domain;

import java.time.LocalDateTime;

public record FileUploadUrlIssueResult(
    Long fileId,
    String uploadUrl,
    String fileKey,
    LocalDateTime expiresAt
) {
    public static FileUploadUrlIssueResult of(File file, UploadUrl uploadUrl) {
        return new FileUploadUrlIssueResult(file.getId(), uploadUrl.url(), file.getFileKey(), uploadUrl.expiresAt());
    }
}
