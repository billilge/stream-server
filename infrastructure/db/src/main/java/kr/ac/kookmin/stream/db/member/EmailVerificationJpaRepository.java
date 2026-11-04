package kr.ac.kookmin.stream.db.member;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationJpaRepository extends JpaRepository<EmailVerificationJpaEntity, Long> {

    Optional<EmailVerificationJpaEntity> findFirstByMemberIdOrderBySentAtDescIdDesc(Long memberId);

    long countByMemberIdAndSentAtGreaterThanEqual(Long memberId, LocalDateTime since);
}
