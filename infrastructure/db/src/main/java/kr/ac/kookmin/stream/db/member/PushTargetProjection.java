package kr.ac.kookmin.stream.db.member;

public record PushTargetProjection(Long memberId, String fcmToken, MemberNotificationSettingJpaEntity setting) {}
