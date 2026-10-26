package kr.ac.kookmin.stream.api.admin.welfare.fee.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;

/**
 * 납부 상태 변경. 안내 문구는 확인필요(REJECTED)일 때만 필수라, 그 판단은 도메인이 한다.
 */
public record FeeStatusUpdateRequest(
    @Schema(description = "변경할 납부 상태", example = "REJECTED")
    @NotNull(message = "변경할 납부 상태를 입력해 주세요.")
    TransferStatus status,

    @Schema(description = "관리자 안내 문구 (최대 255자). 확인필요일 때 필수",
        example = "입금자 정보를 확인하지 못했어요. 문제가 있을 경우 학생회에 문의해 주세요.")
    @Size(max = 255, message = "안내 문구는 255자 이하로 입력해 주세요.")
    String adminNote
) {
}
