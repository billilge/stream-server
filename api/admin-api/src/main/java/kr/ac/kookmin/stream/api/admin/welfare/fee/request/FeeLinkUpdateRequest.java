package kr.ac.kookmin.stream.api.admin.welfare.fee.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record FeeLinkUpdateRequest(
    @NotBlank(message = "송금 링크를 입력해 주세요.")
    @Pattern(regexp = "^https://.*", message = "송금 링크는 https:// 로 시작해야 합니다.")
    @Size(max = 255, message = "송금 링크는 255자를 넘을 수 없습니다.")
    String transferLinkUrl
) {
}
