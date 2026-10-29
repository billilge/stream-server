package kr.ac.kookmin.stream.member.domain.notification.service;

import kr.ac.kookmin.stream.member.domain.notification.domain.MemberNotificationSetting;
import kr.ac.kookmin.stream.member.domain.notification.domain.MemberNotificationSettingUpdateCommand;

public interface MemberNotificationSettingService {
    MemberNotificationSetting getByMemberId(Long memberId);
    void createDefaultIfAbsent(Long memberId);
    MemberNotificationSetting update(Long memberId, MemberNotificationSettingUpdateCommand command);
}
