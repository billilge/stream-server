package kr.ac.kookmin.stream.member.domain.verification.client;

import kr.ac.kookmin.stream.member.domain.verification.domain.EmailMessage;

public interface EmailClient {
    /** 메일 서버에 넘기지 못하면 {@code EmailVerificationErrorCode.EMAIL_SEND_FAILED}를 던진다. */
    void send(EmailMessage message);
    /** 요청 스레드 밖에서 보낸다. 실패는 로그로만 남기고, 발송 대기열이 가득 차면 {@code TaskRejectedException}을 던진다. */
    void sendAsync(EmailMessage message);
}
