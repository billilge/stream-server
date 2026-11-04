package kr.ac.kookmin.stream.api.admin.internal.display.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record DisplayPosterActiveRequest(
    @Schema(description = "활성화 여부. true면 디스플레이에 노출된다", example = "false")
    @NotNull(message = "활성화 여부를 입력해 주세요.")
    Boolean active
) {
}
