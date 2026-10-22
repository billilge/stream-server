package kr.ac.kookmin.stream.api.app.welfare.fee.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 납부 정보 제출. 남은 학기 수와 그 근거가 되는 학점이수현황 캡처를 받는다.
 * <p>
 * 검증에 Bean Validation을 쓰지 않는다 — 명세가 항목마다 전용 에러 코드를 요구하는데
 * 애노테이션으로는 공통 INVALID_INPUT이 되기 때문이다(coding-style 검증 규칙, fee-domain-study 08번 문서).
 */
public record FeeSubmitRequest(
    @Schema(description = "남은 학기 수 (1~8)", example = "6")
    Integer remainingSemesters,

    @Schema(description = "업로드 URL 발급 때 받은 이미지 키", example = "files/3f2a9c1e-7b4d-4e8a-9c2f-1a2b3c4d5e6f.png")
    String proofImageKey
) {
}
