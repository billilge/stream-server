package kr.ac.kookmin.stream.api.admin.internal.file.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.internal.domain.file.domain.FileUploadUrlIssueResult;

public record FileUploadUrlIssueResponse(
    Long fileId,
    String uploadUrl,
    String fileKey,
    LocalDateTime expiresAt
) {
    public static FileUploadUrlIssueResponse from(FileUploadUrlIssueResult result) {
        return new FileUploadUrlIssueResponse(result.fileId(), result.uploadUrl(), result.fileKey(), result.expiresAt());
    }
}
