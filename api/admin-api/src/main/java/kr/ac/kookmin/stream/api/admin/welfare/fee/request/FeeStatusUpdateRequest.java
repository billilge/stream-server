package kr.ac.kookmin.stream.api.admin.welfare.fee.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 납부 상태 변경. 상태 문자열 파싱은 {@code TransferStatus.from}이 하고,
 * 안내 문구는 확인필요일 때만 필수라 Bean Validation 대신 UseCase에서 검증한다.
 */
public record FeeStatusUpdateRequest(
    @Schema(description = "변경할 납부 상태", example = "REJECTED")
    String status,

    @Schema(description = "관리자 안내 문구 (최대 255자). 확인필요일 때 필수",
        example = "입금자 정보를 확인하지 못했어요. 문제가 있을 경우 학생회에 문의해 주세요.")
    String adminNote
) {
}
