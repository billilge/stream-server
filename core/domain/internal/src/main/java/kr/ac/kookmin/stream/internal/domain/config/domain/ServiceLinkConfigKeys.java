package kr.ac.kookmin.stream.internal.domain.config.domain;

// admin_config_values에 저장되는 학생 앱 외부 링크(설정 화면·학생회비 캡처 단계) 키. 값은 그대로 열 수 있는 전체 URL이다.
public final class ServiceLinkConfigKeys {

    // 설정 화면 '의견 보내기'가 여는 구글폼. 앱 서비스에 대한 의견이라 열린피드백과는 다르다
    public static final String FEEDBACK_FORM_URL = "SERVICE_FEEDBACK_FORM_URL";
    // 설정 화면 '개인정보 처리방침'이 여는 노션 페이지
    public static final String PRIVACY_POLICY_URL = "SERVICE_PRIVACY_POLICY_URL";
    // 학생회비 송금의 캡처 단계에서 학점이수현황을 확인하러 여는 K스마트멘토
    public static final String K_SMART_MENTOR_URL = "SERVICE_K_SMART_MENTOR_URL";

    private ServiceLinkConfigKeys() {}
}
