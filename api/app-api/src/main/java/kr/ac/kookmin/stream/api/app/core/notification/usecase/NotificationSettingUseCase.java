package kr.ac.kookmin.stream.api.app.core.notification.usecase;

import kr.ac.kookmin.stream.member.domain.member.domain.TermType;
import kr.ac.kookmin.stream.member.domain.member.service.MemberTermService;
import kr.ac.kookmin.stream.member.domain.notification.domain.MemberNotificationSetting;
import kr.ac.kookmin.stream.member.domain.notification.domain.MemberNotificationSettingUpdateCommand;
import kr.ac.kookmin.stream.member.domain.notification.service.MemberNotificationSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 알림 설정 조회·수정. {@code notification}(카테고리별 수신 설정)과 {@code member}(전체 알림 수신 동의) 두 도메인을 조합한다. */
@Component
@RequiredArgsConstructor
public class NotificationSettingUseCase {

    private final MemberNotificationSettingService memberNotificationSettingService;
    private final MemberTermService memberTermService;

    public NotificationSettingResult getSetting(Long memberId) {
        return toResult(memberId, memberNotificationSettingService.getByMemberId(memberId));
    }

    public NotificationSettingResult updateSetting(Long memberId, MemberNotificationSettingUpdateCommand command) {
        return toResult(memberId, memberNotificationSettingService.update(memberId, command));
    }

    // 전체 알림 동의 자체를 바꾼다. 카테고리별 설정은 그대로 두고, 동의 여부만 바뀐다
    public NotificationSettingResult updateConsent(Long memberId, boolean agreed) {
        memberTermService.updateAgreement(memberId, TermType.NOTIFICATION_CONSENT, agreed);
        return getSetting(memberId);
    }

    private NotificationSettingResult toResult(Long memberId, MemberNotificationSetting setting) {
        boolean notificationConsentAgreed = memberTermService.hasAgreed(memberId, TermType.NOTIFICATION_CONSENT);
        return new NotificationSettingResult(setting, notificationConsentAgreed);
    }
}
