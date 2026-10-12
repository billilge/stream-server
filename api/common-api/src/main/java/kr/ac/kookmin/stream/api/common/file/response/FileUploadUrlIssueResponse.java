package kr.ac.kookmin.stream.api.common.file.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.file.domain.FileUploadUrlIssueResult;

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
