package kr.ac.kookmin.stream.member.domain.notification.domain;

/**
 * 푸시를 받을 수 있는 회원(토큰이 있는 활성 회원)과 그 회원의 알림 설정.
 */
public record PushTarget(Long memberId, String fcmToken, MemberNotificationSetting setting) {

    public boolean accepts(NotificationCategory category) {
        return setting.isEnabled(category);
    }
}
