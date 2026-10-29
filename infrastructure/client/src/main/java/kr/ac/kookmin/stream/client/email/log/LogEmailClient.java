package kr.ac.kookmin.stream.client.email.log;

import kr.ac.kookmin.stream.member.domain.verification.client.EmailClient;
import kr.ac.kookmin.stream.member.domain.verification.domain.EmailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 로컬용 구현체. 발송하지 않고 인증 코드까지 로그로 남기므로 운영 환경에서는 쓰지 않는다.
 */
@Component
@ConditionalOnProperty(prefix = "email", name = "type", havingValue = "log", matchIfMissing = true)
public class LogEmailClient implements EmailClient {

    private static final Logger log = LoggerFactory.getLogger(LogEmailClient.class);

    @Override
    public void send(EmailMessage message) {
        log.info("메일 발송(로그 모드): to={}, subject={}, template={}, variables={}",
            message.to(), message.subject(), message.template(), message.variables());
    }
}
