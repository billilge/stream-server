package kr.ac.kookmin.stream.member.domain.notification.repository;

import java.util.Optional;
import kr.ac.kookmin.stream.member.domain.notification.domain.MemberNotificationSetting;

public interface MemberNotificationSettingRepository {
    Optional<MemberNotificationSetting> findByMemberId(Long memberId);
    boolean existsByMemberId(Long memberId);
    MemberNotificationSetting save(MemberNotificationSetting setting);
}
