package kr.ac.kookmin.stream.member.domain.verification.service;

import kr.ac.kookmin.stream.member.domain.verification.domain.EmailVerificationConfirmCommand;
import kr.ac.kookmin.stream.member.domain.verification.domain.EmailVerificationSendCommand;

public interface EmailVerificationService {
    void sendCode(Long memberId, EmailVerificationSendCommand command);
    void confirm(Long memberId, EmailVerificationConfirmCommand command);
    boolean isVerifiedForSignUp(Long memberId, String email);
}
