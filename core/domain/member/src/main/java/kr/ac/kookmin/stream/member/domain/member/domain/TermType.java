package kr.ac.kookmin.stream.member.domain.member.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum TermType {

    PRIVACY_POLICY(true, "v1"),        // 개인정보 이용 동의
    TERMS_OF_SERVICE(true, "v1"),      // 서비스 이용 약관 동의
    NOTIFICATION_CONSENT(false, "v1"); // 정보 알림 수신 동의(선택). 동의해야 알림 설정에서 카테고리별 수신을 켤 수 있다

    /** 가입 시 반드시 동의해야 하는 약관인지 여부 */
    private final boolean required;

    /** 현재 약관 버전. 동의 기록에 함께 남긴다 */
    private final String version;
}
