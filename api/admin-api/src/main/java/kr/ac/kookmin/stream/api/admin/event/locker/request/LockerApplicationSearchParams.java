package kr.ac.kookmin.stream.api.admin.event.locker.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplicationCancelStatus;

/**
 * 신청 현황 조회 조건. 회차 식별자를 {@link LockerPeriodParams}와 나눠 받는다 — 그쪽은 구역 조회와 함께 쓰는
 * 레코드라 여기에만 있는 검색어·상태가 구역 조회에까지 딸려 가지 않게 한다.
 * <p>
 * {@code @RequestParam}으로 받지 않는 이유는 {@link LockerPeriodParams}와 같다.
 *
 * @param lockerPeriodId 조회할 사물함 운영 회차 식별자
 */
public record LockerApplicationSearchParams(

    @NotNull(message = "사물함 운영 회차를 입력해 주세요.")
    @Schema(description = "조회할 사물함 운영 회차 식별자", example = "1")
    Long lockerPeriodId,

    @Schema(description = "이름 또는 학번 검색어", example = "김학생")
    String keyword,

    @Schema(description = "신청 상태 필터. APPLIED(신청완료) 또는 CANCELED(취소). 없으면 전체", example = "APPLIED")
    String status
) {

    // 빈 검색어는 검색하지 않는 것으로 본다
    public String toKeyword() {
        return keyword == null || keyword.isBlank() ? null : keyword.strip();
    }

    public LockerApplicationCancelStatus toStatus() {
        return LockerApplicationCancelStatus.from(status);
    }
}
