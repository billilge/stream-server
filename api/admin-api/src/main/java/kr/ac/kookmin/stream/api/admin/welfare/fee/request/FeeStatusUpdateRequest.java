package kr.ac.kookmin.stream.api.admin.welfare.fee.request;

import jakarta.validation.constraints.NotBlank;

public record FeeStatusUpdateRequest(
    @NotBlank(message = "처리 상태를 입력해 주세요.")
    String status
) {
}
