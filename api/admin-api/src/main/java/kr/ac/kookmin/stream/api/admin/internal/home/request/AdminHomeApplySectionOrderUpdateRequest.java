package kr.ac.kookmin.stream.api.admin.internal.home.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplyTarget;

/**
 * 신청 섹션 순서 일괄 변경 입력값. 거르지 않은 후보 목록 전체를 바뀐 순서대로 보낸다 — 빠진 대상의 옛
 * 순서값이 남아 새로 매긴 값과 겹친다.
 *
 * @param targets 바뀐 순서대로의 대상
 */
public record AdminHomeApplySectionOrderUpdateRequest(

    @Schema(description = "바뀐 순서대로의 대상 전체")
    @NotNull(message = "신청 섹션 순서를 입력해 주세요.")
    List<@Valid @NotNull(message = "대상은 비워 둘 수 없습니다.") AdminHomeApplyTargetParam> targets
) {

    public List<HomeApplyTarget> toTargets() {
        return targets.stream().map(AdminHomeApplyTargetParam::toTarget).toList();
    }
}
