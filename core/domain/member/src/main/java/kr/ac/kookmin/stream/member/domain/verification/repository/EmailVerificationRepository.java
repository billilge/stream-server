package kr.ac.kookmin.stream.member.domain.verification.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import kr.ac.kookmin.stream.member.domain.verification.domain.EmailVerification;

public interface EmailVerificationRepository {
    Optional<EmailVerification> findLatestByMemberId(Long memberId);
    long countSentSince(Long memberId, LocalDateTime since);
    EmailVerification save(EmailVerification verification);
}
