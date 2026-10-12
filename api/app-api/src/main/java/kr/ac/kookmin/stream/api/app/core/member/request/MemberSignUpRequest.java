package kr.ac.kookmin.stream.api.app.core.member.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberSignUpCommand;
import kr.ac.kookmin.stream.member.domain.member.domain.TermType;

public record MemberSignUpRequest(
    @NotBlank(message = "전화번호를 입력해 주세요.")
    @Pattern(regexp = "^010-?\\d{4}-?\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
    String phoneNumber,

    // 필수 약관 여부는 TermType이 정하므로 여기서는 동의 값을 강제하지 않는다
    @NotEmpty(message = "약관 동의 여부를 입력해 주세요.")
    @Valid
    List<@NotNull(message = "약관 동의 항목이 비어 있습니다.") TermAgreementRequest> termAgreements
) {

    public MemberSignUpCommand toCommand() {
        validateTermTypesNotDuplicated();

        Map<TermType, Boolean> agreements = new EnumMap<>(TermType.class);
        termAgreements.forEach(agreement -> agreements.put(agreement.termType(), agreement.agreed()));
        return new MemberSignUpCommand(phoneNumber, agreements);
    }

    // 같은 약관이 두 번 오면 IllegalArgumentException → GlobalExceptionHandler가 INVALID_INPUT(400)으로 응답한다
    private void validateTermTypesNotDuplicated() {
        long distinctTermTypeCount = termAgreements.stream()
            .map(TermAgreementRequest::termType)
            .distinct()
            .count();
        if (distinctTermTypeCount != termAgreements.size()) {
            throw new IllegalArgumentException("같은 약관의 동의 여부가 중복되었습니다.");
        }
    }

    public record TermAgreementRequest(
        @NotNull(message = "약관 종류를 입력해 주세요.")
        TermType termType,

        @NotNull(message = "약관 동의 여부를 입력해 주세요.")
        Boolean agreed
    ) {}
}
