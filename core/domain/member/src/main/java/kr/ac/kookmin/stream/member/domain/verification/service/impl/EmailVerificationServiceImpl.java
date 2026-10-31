package kr.ac.kookmin.stream.member.domain.verification.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.DateUtil;
import kr.ac.kookmin.stream.member.domain.verification.client.EmailClient;
import kr.ac.kookmin.stream.member.domain.verification.domain.EmailMessage;
import kr.ac.kookmin.stream.member.domain.verification.domain.EmailTemplate;
import kr.ac.kookmin.stream.member.domain.verification.domain.EmailVerification;
import kr.ac.kookmin.stream.member.domain.verification.domain.EmailVerificationConfirmCommand;
import kr.ac.kookmin.stream.member.domain.verification.domain.EmailVerificationErrorCode;
import kr.ac.kookmin.stream.member.domain.verification.domain.EmailVerificationSendCommand;
import kr.ac.kookmin.stream.member.domain.verification.repository.EmailVerificationRepository;
import kr.ac.kookmin.stream.member.domain.verification.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class EmailVerificationServiceImpl implements EmailVerificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailVerificationServiceImpl.class);

    private static final int MAX_SENDS_PER_DAY = 10;
    private static final int CODE_BOUND = 1_000_000;
    private static final String CODE_FORMAT = "%06d";
    private static final String CODE_MAIL_SUBJECT = "[소프트웨어융합대학 학생회] 이메일 인증 코드";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailClient emailClient;

    @Override
    public void sendCode(Long memberId, EmailVerificationSendCommand command) {
        LocalDateTime now = LocalDateTime.now(DateUtil.KST);
        validateNotInResendCooldown(memberId, now);
        validateDailyLimitNotExceeded(memberId, now);

        String code = generateCode();
        sendCodeMail(command.email(), code);
        emailVerificationRepository.save(EmailVerification.create(memberId, command.email(), code, now));
    }

    // 트랜잭션을 걸면 틀린 횟수 저장이 예외와 함께 롤백되므로 걸지 않는다
    @Override
    public void confirm(Long memberId, EmailVerificationConfirmCommand command) {
        LocalDateTime now = LocalDateTime.now(DateUtil.KST);
        EmailVerification verification = getLatestFor(memberId, command.email());
        if (verification.isVerified()) {
            return;
        }
        validateNotExpired(verification, now);
        validateFailedAttemptsNotExceeded(verification);
        validateCodeMatched(verification, command.code());

        verification.verify(now);
        emailVerificationRepository.save(verification);
    }

    @Override
    public boolean isVerifiedForSignUp(Long memberId, String email) {
        LocalDateTime now = LocalDateTime.now(DateUtil.KST);
        return emailVerificationRepository.findLatestByMemberId(memberId)
            .map(verification -> verification.canSignUpWith(email, now))
            .orElse(false);
    }

    private EmailVerification getLatestFor(Long memberId, String email) {
        return emailVerificationRepository.findLatestByMemberId(memberId)
            .filter(verification -> verification.isFor(email))
            .orElseThrow(() -> new BusinessException(EmailVerificationErrorCode.EMAIL_VERIFICATION_NOT_FOUND));
    }

    private void validateNotInResendCooldown(Long memberId, LocalDateTime now) {
        boolean inCooldown = emailVerificationRepository.findLatestByMemberId(memberId)
            .map(latest -> latest.isInResendCooldown(now))
            .orElse(false);
        if (inCooldown) {
            throw new BusinessException(EmailVerificationErrorCode.EMAIL_VERIFICATION_COOLDOWN);
        }
    }

    private void validateDailyLimitNotExceeded(Long memberId, LocalDateTime now) {
        long sentToday = emailVerificationRepository.countSentSince(memberId, now.toLocalDate().atStartOfDay());
        if (sentToday >= MAX_SENDS_PER_DAY) {
            throw new BusinessException(EmailVerificationErrorCode.EMAIL_VERIFICATION_DAILY_LIMIT_EXCEEDED);
        }
    }

    private void validateNotExpired(EmailVerification verification, LocalDateTime now) {
        if (verification.isExpired(now)) {
            throw new BusinessException(EmailVerificationErrorCode.EMAIL_VERIFICATION_EXPIRED);
        }
    }

    private void validateFailedAttemptsNotExceeded(EmailVerification verification) {
        if (verification.hasExceededFailedAttempts()) {
            throw new BusinessException(EmailVerificationErrorCode.EMAIL_VERIFICATION_ATTEMPTS_EXCEEDED);
        }
    }

    private void validateCodeMatched(EmailVerification verification, String code) {
        if (verification.matchesCode(code)) {
            return;
        }
        verification.recordFailedAttempt();
        emailVerificationRepository.save(verification);
        if (verification.hasExceededFailedAttempts()) {
            throw new BusinessException(EmailVerificationErrorCode.EMAIL_VERIFICATION_ATTEMPTS_EXCEEDED);
        }
        throw new BusinessException(EmailVerificationErrorCode.EMAIL_VERIFICATION_CODE_MISMATCH);
    }

    private void sendCodeMail(String email, String code) {
        try {
            emailClient.sendAsync(createCodeMessage(email, code));
        } catch (TaskRejectedException e) {
            log.warn("인증 메일 발송 대기열이 가득 차 요청을 거절했습니다");
            throw new BusinessException(EmailVerificationErrorCode.EMAIL_SEND_FAILED);
        }
    }

    private String generateCode() {
        return CODE_FORMAT.formatted(RANDOM.nextInt(CODE_BOUND));
    }

    private EmailMessage createCodeMessage(String email, String code) {
        Map<String, Object> variables = Map.of(
            "code", code,
            "expiresInMinutes", EmailVerification.CODE_VALIDITY.toMinutes()
        );
        return new EmailMessage(email, CODE_MAIL_SUBJECT, EmailTemplate.VERIFICATION_CODE, variables);
    }
}
