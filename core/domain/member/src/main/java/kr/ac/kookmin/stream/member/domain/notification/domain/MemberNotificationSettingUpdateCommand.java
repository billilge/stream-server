package kr.ac.kookmin.stream.member.domain.notification.domain;

/**
 * 알림 설정 수정 요청. 전체 알림은 저장하지 않으므로 네 카테고리 값을 모두 받아 그대로 덮어쓴다.
 */
public record MemberNotificationSettingUpdateCommand(
    boolean rentalEnabled,
    boolean eventEnabled,
    boolean lockerEnabled,
    boolean noticeEnabled
) {}
