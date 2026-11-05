package kr.ac.kookmin.stream.api.admin.internal.home.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplyTarget;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplyTargetType;

/**
 * 신청 섹션 노출 여부 변경 입력값. 대상을 경로가 아니라 본문으로 받는다 — 경로·쿼리의 enum 변환 실패는
 * {@code GlobalExceptionHandler}가 받지 않아 500이 된다.
 */
public record AdminHomeApplySectionExposureRequest(

    @Schema(description = "대상 종류")
    @NotNull(message = "대상 종류를 입력해 주세요.")
    HomeApplyTargetType targetType,

    @Schema(description = "EVENT면 행사 ID, LOCKER면 사물함 운영 회차 ID", example = "17")
    @NotNull(message = "대상 ID를 입력해 주세요.")
    Long targetId,

    @Schema(description = "홈에 노출할지", example = "true")
    @NotNull(message = "노출 여부를 입력해 주세요.")
    Boolean isExposed
) {

    public HomeApplyTarget toTarget() {
        return new HomeApplyTarget(targetType, targetId);
    }
}
