package kr.ac.kookmin.stream.api.admin.welfare.fee.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record FeeLinkUpdateRequest(
    @NotBlank(message = "은행명을 입력해 주세요.")
    String bank,

    @NotBlank(message = "계좌번호를 입력해 주세요.")
    @Pattern(regexp = "^[0-9-]+$", message = "계좌번호는 숫자와 '-'만 입력할 수 있습니다.")
    String accountNo
) {
}
