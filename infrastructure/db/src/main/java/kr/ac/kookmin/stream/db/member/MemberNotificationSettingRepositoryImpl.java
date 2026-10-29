package kr.ac.kookmin.stream.db.member;

import java.util.Optional;
import kr.ac.kookmin.stream.member.domain.notification.domain.MemberNotificationSetting;
import kr.ac.kookmin.stream.member.domain.notification.repository.MemberNotificationSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberNotificationSettingRepositoryImpl implements MemberNotificationSettingRepository {

    private final MemberNotificationSettingJpaRepository memberNotificationSettingJpaRepository;

    @Override
    public Optional<MemberNotificationSetting> findByMemberId(Long memberId) {
        return memberNotificationSettingJpaRepository.findByMemberId(memberId)
            .map(MemberNotificationSettingJpaEntity::toDomain);
    }

    @Override
    public boolean existsByMemberId(Long memberId) {
        return memberNotificationSettingJpaRepository.existsByMemberId(memberId);
    }

    @Override
    public MemberNotificationSetting save(MemberNotificationSetting setting) {
        return memberNotificationSettingJpaRepository.save(MemberNotificationSettingJpaEntity.from(setting)).toDomain();
    }
}
