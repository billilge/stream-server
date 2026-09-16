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
    private boolean noticeEnabled;

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
}
