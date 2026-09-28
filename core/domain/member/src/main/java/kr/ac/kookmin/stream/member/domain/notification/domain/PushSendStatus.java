package kr.ac.kookmin.stream.member.domain.notification.domain;

public enum PushSendStatus {
    SUCCESS,
    INVALID_TOKEN,  // 만료·삭제되어 다시 보내도 성공할 수 없는 토큰. fcm_token 정리 대상
    RETRYABLE,      // 일시적 장애로 실패. 해당 토큰만 재시도 대상
    FAILED          // 설정 문제(자격증명, APNs 키, 다른 Firebase 프로젝트 등). 설정을 고치기 전에는 재시도해도 실패
}
