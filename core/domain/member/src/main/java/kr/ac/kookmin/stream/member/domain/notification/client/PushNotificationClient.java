package kr.ac.kookmin.stream.member.domain.notification.client;

import java.util.List;
import kr.ac.kookmin.stream.member.domain.notification.domain.PushMessage;
import kr.ac.kookmin.stream.member.domain.notification.domain.PushSendResult;

public interface PushNotificationClient {
    PushSendResult send(List<String> tokens, PushMessage message);
}
