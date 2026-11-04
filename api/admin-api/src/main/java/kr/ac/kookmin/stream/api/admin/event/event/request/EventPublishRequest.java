package kr.ac.kookmin.stream.api.admin.event.event.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record EventPublishRequest(
    @Schema(description = "공개 여부. true면 학생 앱에 노출된다", example = "true")
    @NotNull(message = "공개 여부를 입력해 주세요.")
    Boolean isPublished
) {
}
