package kr.ac.kookmin.stream.member.domain.notification.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberNotificationSetting {

    private Long id;
    private Long memberId;
    private boolean rentalEnabled;
    private boolean eventEnabled;
    private boolean lockerEnabled;
    // 게시판 알림. 새 공지와 열린피드백 답변을 함께 다룬다
    private boolean noticeEnabled;

    // 회원가입할 때 만든다. 알림 수신 동의(NOTIFICATION_CONSENT) 약관에 동의했으면 전부 켜고, 동의하지 않았으면 전부 끈다
    public static MemberNotificationSetting createDefault(Long memberId, boolean notificationConsentAgreed) {
        return new MemberNotificationSetting(
            null, memberId, notificationConsentAgreed, notificationConsentAgreed, notificationConsentAgreed, notificationConsentAgreed);
    }

    public static MemberNotificationSetting of(
        Long id,
        Long memberId,
        boolean rentalEnabled,
        boolean eventEnabled,
        boolean lockerEnabled,
        boolean noticeEnabled
    ) {
        return new MemberNotificationSetting(id, memberId, rentalEnabled, eventEnabled, lockerEnabled, noticeEnabled);
    }

    // 네 카테고리가 모두 꺼져 있는지. 전체 알림 동의를 켤 때 같이 켜 줄지 판단하는 데 쓴다
    public boolean allDisabled() {
        return !rentalEnabled && !eventEnabled && !lockerEnabled && !noticeEnabled;
    }

    public boolean isEnabled(NotificationCategory category) {
        return switch (category) {
            case BILLILGE -> rentalEnabled;
            case EVENT -> eventEnabled;
            case LOCKER -> lockerEnabled;
            case NOTICE -> noticeEnabled;
        };
    }

    // 바뀐 값이 있으면 true를 돌려준다
    public boolean update(MemberNotificationSettingUpdateCommand command) {
        if (this.rentalEnabled == command.rentalEnabled()
            && this.eventEnabled == command.eventEnabled()
            && this.lockerEnabled == command.lockerEnabled()
            && this.noticeEnabled == command.noticeEnabled()) {
            return false;
        }
        this.rentalEnabled = command.rentalEnabled();
        this.eventEnabled = command.eventEnabled();
        this.lockerEnabled = command.lockerEnabled();
        this.noticeEnabled = command.noticeEnabled();
        return true;
    }
}
