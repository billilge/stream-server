package kr.ac.kookmin.stream.api.admin.welfare.fee.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 회비 납부 설정 변경. 계좌와 학기당 회비를 한 화면에서 같이 저장하므로 함께 받는다.
 * <p>
 * 검증에 Bean Validation을 쓰지 않는다 — 항목마다 전용 에러 코드를 내려줘야 하는데
 * 애노테이션으로는 공통 INVALID_INPUT이 되기 때문이다(fee-domain-study 08번 문서).
 */
public record FeeAccountUpdateRequest(
    @Schema(description = "은행명 (허용 목록 중 하나)", example = "카카오뱅크")
    String bank,

    @Schema(description = "계좌번호 (하이픈 제외, 10~14자리)", example = "3333011234567")
    String accountNo,

    @Schema(description = "학기당 학생회비", example = "20000")
    Integer amount
) {
}
