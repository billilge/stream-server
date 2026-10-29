package kr.ac.kookmin.stream.db.member;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberNotificationSettingJpaRepository extends JpaRepository<MemberNotificationSettingJpaEntity, Long> {

    Optional<MemberNotificationSettingJpaEntity> findByMemberId(Long memberId);

    boolean existsByMemberId(Long memberId);
}
