package kr.ac.kookmin.stream.member.domain.verification.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HexFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 이메일 인증 코드 발송 기록. 코드는 원문 없이 SHA-256 해시로만 저장한다.
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailVerification {

    public static final Duration CODE_VALIDITY = Duration.ofMinutes(5);
    public static final Duration RESEND_COOLDOWN = Duration.ofSeconds(60);
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Duration SIGN_UP_VALIDITY = Duration.ofMinutes(30);

    private Long id;
    private Long memberId;
    private String email;
    private String codeHash;
    private LocalDateTime sentAt;
    private int failedAttempts;
    private LocalDateTime verifiedAt;

    public static EmailVerification create(Long memberId, String email, String code, LocalDateTime now) {
        return new EmailVerification(null, memberId, email, hash(code), now, 0, null);
    }

    public static EmailVerification of(
        Long id,
        Long memberId,
        String email,
        String codeHash,
        LocalDateTime sentAt,
        int failedAttempts,
        LocalDateTime verifiedAt
    ) {
        return new EmailVerification(id, memberId, email, codeHash, sentAt, failedAttempts, verifiedAt);
    }

    public boolean isFor(String email) {
        return this.email.equals(email);
    }

    public boolean isVerified() {
        return verifiedAt != null;
    }

    public boolean isExpired(LocalDateTime now) {
        return !now.isBefore(sentAt.plus(CODE_VALIDITY));
    }

    public boolean isInResendCooldown(LocalDateTime now) {
        return now.isBefore(sentAt.plus(RESEND_COOLDOWN));
    }

    public boolean hasExceededFailedAttempts() {
        return failedAttempts >= MAX_FAILED_ATTEMPTS;
    }

    public boolean matchesCode(String code) {
        return codeHash.equals(hash(code));
    }

    public void recordFailedAttempt() {
        failedAttempts++;
    }

    public void verify(LocalDateTime now) {
        verifiedAt = now;
    }

    public boolean canSignUpWith(String email, LocalDateTime now) {
        return isFor(email) && isVerified() && now.isBefore(verifiedAt.plus(SIGN_UP_VALIDITY));
    }

    private static String hash(String code) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(code.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256을 지원하지 않는 JVM입니다", e);
        }
    }
}
