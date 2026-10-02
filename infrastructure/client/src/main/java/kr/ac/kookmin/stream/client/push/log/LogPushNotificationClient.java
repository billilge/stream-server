package kr.ac.kookmin.stream.client.push.log;

import java.util.List;
import kr.ac.kookmin.stream.member.domain.notification.client.PushNotificationClient;
import kr.ac.kookmin.stream.member.domain.notification.domain.PushMessage;
import kr.ac.kookmin.stream.member.domain.notification.domain.PushSendResult;
import kr.ac.kookmin.stream.member.domain.notification.domain.PushSendStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Firebase 자격증명 없이 기동하기 위한 로컬용 구현체. 실제로 발송하지 않고 로그만 남긴 뒤 전부 성공으로 반환한다.
 */
@Component
@ConditionalOnProperty(prefix = "push", name = "type", havingValue = "log", matchIfMissing = true)
public class LogPushNotificationClient implements PushNotificationClient {

    private static final Logger log = LoggerFactory.getLogger(LogPushNotificationClient.class);

    @Override
    public PushSendResult send(List<String> tokens, PushMessage message) {
        log.info("푸시 발송(로그 모드): tokenCount={}, title={}, body={}, data={}",
            tokens.size(), message.title(), message.body(), message.data());
        return PushSendResult.of(tokens, PushSendStatus.SUCCESS);
    }
}
