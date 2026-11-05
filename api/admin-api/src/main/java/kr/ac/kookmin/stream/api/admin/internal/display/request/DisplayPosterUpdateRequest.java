package kr.ac.kookmin.stream.api.admin.internal.display.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayPosterUpdateCommand;

public record DisplayPosterUpdateRequest(
    @Schema(description = "포스터명", example = "SW 해커톤")
    @NotBlank(message = "포스터명을 입력해 주세요.")
    @Size(max = 100, message = "포스터명은 100자 이하로 입력해 주세요.")
    String title,

    @Schema(description = "포스터 이미지 파일 ID. 이미지를 바꾸면 새로 올린 fileId, 그대로 두면 목록 조회에서 받은 기존 값",
        example = "152")
    @NotNull(message = "포스터 이미지를 입력해 주세요.")
    Long imageFileId
) {

    public DisplayPosterUpdateCommand toCommand() {
        return new DisplayPosterUpdateCommand(title, imageFileId);
    }
}
