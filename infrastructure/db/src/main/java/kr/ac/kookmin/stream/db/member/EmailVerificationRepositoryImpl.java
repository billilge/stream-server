package kr.ac.kookmin.stream.db.member;

import java.time.LocalDateTime;
import java.util.Optional;
import kr.ac.kookmin.stream.member.domain.verification.domain.EmailVerification;
import kr.ac.kookmin.stream.member.domain.verification.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EmailVerificationRepositoryImpl implements EmailVerificationRepository {

    private final EmailVerificationJpaRepository emailVerificationJpaRepository;

    @Override
    public Optional<EmailVerification> findLatestByMemberId(Long memberId) {
        return emailVerificationJpaRepository.findFirstByMemberIdOrderBySentAtDescIdDesc(memberId)
            .map(EmailVerificationJpaEntity::toDomain);
    }

    @Override
    public long countSentSince(Long memberId, LocalDateTime since) {
        return emailVerificationJpaRepository.countByMemberIdAndSentAtGreaterThanEqual(memberId, since);
    }

    @Override
    public EmailVerification save(EmailVerification verification) {
        return emailVerificationJpaRepository.save(EmailVerificationJpaEntity.from(verification)).toDomain();
    }
}
