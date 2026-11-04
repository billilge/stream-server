package kr.ac.kookmin.stream.api.app.core.notification.usecase;

import kr.ac.kookmin.stream.member.domain.notification.domain.MemberNotificationSetting;

// NotificationSettingUseCase가 조합한 결과. DTO(MemberNotificationSettingResponse) 변환은 Controller가 한다.
public record NotificationSettingResult(MemberNotificationSetting setting, boolean notificationConsentAgreed) {
}
