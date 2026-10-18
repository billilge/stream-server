package kr.ac.kookmin.stream.api.app.core.notification.response;

import kr.ac.kookmin.stream.member.domain.notification.domain.MemberNotificationSetting;

public record MemberNotificationSettingResponse(
    boolean rentalEnabled,
    boolean eventEnabled,
    boolean lockerEnabled,
    boolean noticeEnabled
) {
    public static MemberNotificationSettingResponse from(MemberNotificationSetting setting) {
        return new MemberNotificationSettingResponse(
            setting.isRentalEnabled(),
            setting.isEventEnabled(),
            setting.isLockerEnabled(),
            setting.isNoticeEnabled()
        );
    }
}
