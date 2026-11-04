package kr.ac.kookmin.stream.internal.domain.config.domain;

// admin_config_values에 저장되는 학생 앱 설정 화면의 외부 링크 키. 값은 그대로 열 수 있는 전체 URL이다.
public final class ServiceLinkConfigKeys {

    // 설정 화면 '의견 보내기'가 여는 구글폼. 앱 서비스에 대한 의견이라 열린피드백과는 다르다
    public static final String FEEDBACK_FORM_URL = "SERVICE_FEEDBACK_FORM_URL";
    // 설정 화면 '개인정보 처리방침'이 여는 노션 페이지
    public static final String PRIVACY_POLICY_URL = "SERVICE_PRIVACY_POLICY_URL";

    private ServiceLinkConfigKeys() {}
}
