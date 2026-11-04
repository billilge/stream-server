package kr.ac.kookmin.stream.member.domain.verification.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * 템플릿 파일은 infrastructure:client의 resources/templates/email/{templateName}.html에 둔다.
 */
@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum EmailTemplate {

    // 변수: code(인증 코드), expiresInMinutes(유효 시간, 분)
    VERIFICATION_CODE("verification-code");

    private final String templateName;
}
