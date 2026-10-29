package kr.ac.kookmin.stream.member.domain.verification.client;

import kr.ac.kookmin.stream.member.domain.verification.domain.EmailMessage;

public interface EmailClient {
    /** 메일 서버에 넘기지 못하면 {@code EmailVerificationErrorCode.EMAIL_SEND_FAILED}를 던진다. */
    void send(EmailMessage message);
}
