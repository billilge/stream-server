package kr.ac.kookmin.stream.api.admin.welfare.notice.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record NoticePinRequest(
    @Schema(description = "고정 여부. true면 목록 맨 위에 고정하고 false면 해제한다", example = "true")
    @NotNull(message = "고정 여부를 입력해 주세요.")
    Boolean pinned
) {
}
