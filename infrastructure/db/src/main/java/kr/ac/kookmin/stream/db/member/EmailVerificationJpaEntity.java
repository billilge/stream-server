package kr.ac.kookmin.stream.db.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import kr.ac.kookmin.stream.db.common.BaseTimeEntity;
import kr.ac.kookmin.stream.member.domain.verification.domain.EmailVerification;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "email_verifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerificationJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_verification_id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String email;

    @Column(name = "code_hash", nullable = false, length = 64)
    private String codeHash;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    private EmailVerificationJpaEntity(EmailVerification verification) {
        this.id = verification.getId();
        this.memberId = verification.getMemberId();
        this.email = verification.getEmail();
        this.codeHash = verification.getCodeHash();
        this.sentAt = verification.getSentAt();
        this.failedAttempts = verification.getFailedAttempts();
        this.verifiedAt = verification.getVerifiedAt();
    }

    public static EmailVerificationJpaEntity from(EmailVerification verification) {
        return new EmailVerificationJpaEntity(verification);
    }

    public EmailVerification toDomain() {
        return EmailVerification.of(id, memberId, email, codeHash, sentAt, failedAttempts, verifiedAt);
    }
}
