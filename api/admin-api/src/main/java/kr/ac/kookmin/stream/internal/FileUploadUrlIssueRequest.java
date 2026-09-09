package kr.ac.kookmin.stream.internal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.ac.kookmin.stream.internal.domain.file.domain.FileCategory;
import kr.ac.kookmin.stream.internal.domain.file.domain.FileUploadUrlIssueCommand;

public record FileUploadUrlIssueRequest(
    @NotBlank(message = "파일명은 필수 입력값입니다.")
    String originalName,

    @NotBlank(message = "MIME 타입은 필수 입력값입니다.")
    String contentType,

    @Positive(message = "파일 크기는 0보다 커야 합니다.")
    long fileSize,

    @NotNull(message = "파일 카테고리는 필수 입력값입니다.")
    FileCategory category
) {
    public FileUploadUrlIssueCommand toCommand(Long uploaderId) {
        return new FileUploadUrlIssueCommand(originalName, contentType, fileSize, category, uploaderId);
    }
}
