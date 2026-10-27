package kr.ac.kookmin.stream.api.admin.welfare.fee.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;

/**
 * 납부 상태 변경. 반려 사유는 확인필요(REJECTED)일 때만 필수라, 필수 여부와 길이는 도메인이 검증한다.
 */
public record FeeStatusUpdateRequest(
    @Schema(description = "변경할 납부 상태", example = "REJECTED")
    @NotNull(message = "변경할 납부 상태를 입력해 주세요.")
    TransferStatus status,

    @Schema(description = "학생에게 보여줄 반려 사유 (최대 255자). 확인필요일 때 필수",
        example = "입금자 정보를 확인하지 못했어요. 문제가 있을 경우 학생회에 문의해 주세요.")
    String rejectReason
) {
}
