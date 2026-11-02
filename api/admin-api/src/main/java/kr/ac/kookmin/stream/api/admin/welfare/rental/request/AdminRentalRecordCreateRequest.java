package kr.ac.kookmin.stream.api.admin.welfare.rental.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 관리자가 현장 기록을 대신 등록할 때 쓰는 요청. 상태 선택 필드가 없다 — 실제 폼(RentalRecordFormPage)에
 * 상태 선택 UI가 없고, 등록 즉시 수령 완료로 본다(상위 API 명세서 "결정된 사항 6" 참고). 수량 입력 필드도 없어
 * 항상 1건으로 등록한다(AdminRentalRecordCreateUseCase에서 고정).
 */
public record AdminRentalRecordCreateRequest(
    @Schema(description = "대여자 ID", example = "7")
    @NotNull(message = "대여자를 입력해 주세요.")
    Long memberId,

    @Schema(description = "대여할 물품 ID", example = "12")
    @NotNull(message = "대여할 물품을 입력해 주세요.")
    Long itemId,

    @Schema(description = "실제로 물품을 건네준 근무자(관리자) ID. 호출한 관리자 본인과 다를 수 있다", example = "2")
    @NotNull(message = "근무자를 입력해 주세요.")
    Long workerId,

    @Schema(description = "실제 수령(대여 시작) 시각", example = "2026-10-27T13:00:00")
    @NotNull(message = "대여 시작 시각을 입력해 주세요.")
    LocalDateTime rentAt
) {
}
