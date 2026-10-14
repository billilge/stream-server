package kr.ac.kookmin.stream.member.domain.notification.domain;

/**
 * 알림 카테고리. 알림 설정의 수신 여부를 고르는 기준이고, 푸시 data의 {@code category} 값으로 앱에 그대로 내려간다.
 */
public enum NotificationCategory {
    BILLILGE,   // 빌릴게 (rental_enabled)
    EVENT,      // 행사 (event_enabled)
    LOCKER,     // 사물함 (locker_enabled)
    NOTICE      // 게시판: 새 공지, 열린피드백 답변 (notice_enabled)
}
