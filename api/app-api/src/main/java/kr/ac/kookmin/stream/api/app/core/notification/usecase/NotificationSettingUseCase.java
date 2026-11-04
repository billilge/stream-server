package kr.ac.kookmin.stream.api.app.core.notification.usecase;

import kr.ac.kookmin.stream.member.domain.member.domain.TermType;
import kr.ac.kookmin.stream.member.domain.member.service.MemberTermService;
import kr.ac.kookmin.stream.member.domain.notification.domain.MemberNotificationSetting;
import kr.ac.kookmin.stream.member.domain.notification.domain.MemberNotificationSettingUpdateCommand;
import kr.ac.kookmin.stream.member.domain.notification.service.MemberNotificationSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 알림 설정 조회·수정. {@code notification}(카테고리별 수신 설정)과 {@code member}(전체 알림 수신 동의) 두 도메인을 조합한다. */
@Component
@RequiredArgsConstructor
public class NotificationSettingUseCase {

    private static final MemberNotificationSettingUpdateCommand ALL_ENABLED =
        new MemberNotificationSettingUpdateCommand(true, true, true, true);

    private final MemberNotificationSettingService memberNotificationSettingService;
    private final MemberTermService memberTermService;

    public NotificationSettingResult getSetting(Long memberId) {
        return toResult(memberId, memberNotificationSettingService.getByMemberId(memberId));
    }

    public NotificationSettingResult updateSetting(Long memberId, MemberNotificationSettingUpdateCommand command) {
        return toResult(memberId, memberNotificationSettingService.update(memberId, command));
    }

    /**
     * 전체 알림 동의 자체를 바꾼다. 설정을 먼저 읽어 존재를 확인한 뒤 동의를 바꾼다 — 가입을 마치지 않아 설정이
     * 없는 회원이 부르면 쓰기 전에 404가 나고, 동의 기록만 바뀐 채 남는 일이 없다.
     * 가입할 때 동의하지 않아 카테고리가 모두 꺼진 회원이 나중에 동의를 켜면, 네 카테고리도 함께 켠다 —
     * 안 그러면 동의만 켜고 푸시는 하나도 가지 않는다.
     */
    @Transactional
    public NotificationSettingResult updateConsent(Long memberId, boolean agreed) {
        MemberNotificationSetting setting = memberNotificationSettingService.getByMemberId(memberId);
        memberTermService.updateAgreement(memberId, TermType.NOTIFICATION_CONSENT, agreed);
        if (agreed && setting.allDisabled()) {
            setting = memberNotificationSettingService.update(memberId, ALL_ENABLED);
        }
        return new NotificationSettingResult(setting, agreed);
    }

    private NotificationSettingResult toResult(Long memberId, MemberNotificationSetting setting) {
        boolean notificationConsentAgreed = memberTermService.hasAgreed(memberId, TermType.NOTIFICATION_CONSENT);
        return new NotificationSettingResult(setting, notificationConsentAgreed);
    }
}
