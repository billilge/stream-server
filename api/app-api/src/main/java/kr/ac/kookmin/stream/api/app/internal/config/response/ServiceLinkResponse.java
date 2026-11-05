package kr.ac.kookmin.stream.api.app.internal.config.response;

// 링크가 등록되지 않았으면 그 필드는 null이다
public record ServiceLinkResponse(
    // 설정 화면 '의견 보내기'가 여는 구글폼 주소
    String feedbackFormUrl,
    // 설정 화면 '개인정보 처리방침'이 여는 노션 페이지 주소
    String privacyPolicyUrl,
    // 학생회비 송금의 캡처 단계에서 여는 K스마트멘토 주소
    String kSmartMentorUrl
) {}
