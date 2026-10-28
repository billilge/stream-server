package kr.ac.kookmin.stream.member.domain.notification.service.impl;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.member.domain.notification.domain.MemberNotificationSetting;
import kr.ac.kookmin.stream.member.domain.notification.domain.MemberNotificationSettingUpdateCommand;
import kr.ac.kookmin.stream.member.domain.notification.domain.NotificationErrorCode;
import kr.ac.kookmin.stream.member.domain.notification.repository.MemberNotificationSettingRepository;
import kr.ac.kookmin.stream.member.domain.notification.service.MemberNotificationSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class MemberNotificationSettingServiceImpl implements MemberNotificationSettingService {

    private final MemberNotificationSettingRepository memberNotificationSettingRepository;

    @Override
    public MemberNotificationSetting getByMemberId(Long memberId) {
        return memberNotificationSettingRepository.findByMemberId(memberId)
            .orElseThrow(() -> new BusinessException(NotificationErrorCode.NOTIFICATION_SETTING_NOT_FOUND));
    }

    // 필수 약관이 늘어 다시 가입하는 회원은 이미 설정이 있으므로 바꾼 값을 그대로 둔다
    @Override
    @Transactional
    public void createDefaultIfAbsent(Long memberId) {
        if (memberNotificationSettingRepository.existsByMemberId(memberId)) {
            return;
        }
        memberNotificationSettingRepository.save(MemberNotificationSetting.createDefault(memberId));
    }

    @Override
    @Transactional
    public MemberNotificationSetting update(Long memberId, MemberNotificationSettingUpdateCommand command) {
        MemberNotificationSetting setting = getByMemberId(memberId);
        if (!setting.update(command)) {
            return setting;
        }
        return memberNotificationSettingRepository.save(setting);
    }
}
