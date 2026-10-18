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

    // 회원가입할 때 만든다. 처음에는 모든 알림을 받는다
    public static MemberNotificationSetting createDefault(Long memberId) {
        return new MemberNotificationSetting(null, memberId, true, true, true, true);
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
