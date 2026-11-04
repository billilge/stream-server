package kr.ac.kookmin.stream.api.app.core.notification.response;

import kr.ac.kookmin.stream.api.app.core.notification.usecase.NotificationSettingResult;

public record MemberNotificationSettingResponse(
    boolean notificationConsentAgreed,
    boolean rentalEnabled,
    boolean eventEnabled,
    boolean lockerEnabled,
    boolean noticeEnabled
) {
    public static MemberNotificationSettingResponse from(NotificationSettingResult result) {
        return new MemberNotificationSettingResponse(
            result.notificationConsentAgreed(),
            result.setting().isRentalEnabled(),
            result.setting().isEventEnabled(),
            result.setting().isLockerEnabled(),
            result.setting().isNoticeEnabled()
        );
    }
}
