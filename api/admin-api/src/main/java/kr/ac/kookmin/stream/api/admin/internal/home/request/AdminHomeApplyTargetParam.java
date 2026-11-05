package kr.ac.kookmin.stream.api.admin.internal.home.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplyTarget;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplyTargetType;

/**
 * 신청 섹션이 가리키는 대상. 설정 행이 아직 없는 후보도 가리켜야 해서 행 식별자가 아니라 대상으로 받는다.
 */
public record AdminHomeApplyTargetParam(

    @Schema(description = "대상 종류")
    @NotNull(message = "대상 종류를 입력해 주세요.")
    HomeApplyTargetType targetType,

    @Schema(description = "EVENT면 행사 ID, LOCKER면 사물함 운영 회차 ID", example = "17")
    @NotNull(message = "대상 ID를 입력해 주세요.")
    Long targetId
) {

    public HomeApplyTarget toTarget() {
        return new HomeApplyTarget(targetType, targetId);
    }
}
