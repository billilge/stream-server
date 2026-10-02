package kr.ac.kookmin.stream.client.push.fcm;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.member.domain.notification.client.PushNotificationClient;
import kr.ac.kookmin.stream.member.domain.notification.domain.PushMessage;
import kr.ac.kookmin.stream.member.domain.notification.domain.PushSendOutcome;
import kr.ac.kookmin.stream.member.domain.notification.domain.PushSendResult;
import kr.ac.kookmin.stream.member.domain.notification.domain.PushSendStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Firebase Admin SDK 기반 FCM 발송 구현체. 푸시 실패가 호출 측의 본 흐름을 깨지 않도록,
 * 발송 요청 자체가 실패해도 예외를 던지지 않고 로그를 남긴 뒤 토큰별 실패 상태로 반환한다.
 * <p>
 * 알림은 notification 페이로드(title/body)로 보내 백그라운드 표시를 OS에 맡긴다. data-only는 iOS에서 무음 푸시로
 * 취급돼 전달이 제한되기 때문이다. 앱이 백그라운드 핸들러에서 로컬 알림을 또 띄우면 알림이 두 번 뜨므로,
 * 앱은 포그라운드에서만 직접 표시한다.
 */
@Component
@ConditionalOnProperty(prefix = "push", name = "type", havingValue = "fcm")
@RequiredArgsConstructor
public class FcmPushNotificationClient implements PushNotificationClient {

    private static final Logger log = LoggerFactory.getLogger(FcmPushNotificationClient.class);

    // FCM 멀티캐스트 한 번에 담을 수 있는 최대 토큰 수
    private static final int MAX_TOKENS_PER_REQUEST = 500;

    private final FirebaseMessaging firebaseMessaging;

    @Override
    public PushSendResult send(List<String> tokens, PushMessage message) {
        List<PushSendOutcome> outcomes = new ArrayList<>(tokens.size());
        for (int from = 0; from < tokens.size(); from += MAX_TOKENS_PER_REQUEST) {
            List<String> chunk = tokens.subList(from, Math.min(from + MAX_TOKENS_PER_REQUEST, tokens.size()));
            outcomes.addAll(sendChunk(chunk, message).outcomes());
        }
        return new PushSendResult(outcomes);
    }

    private PushSendResult sendChunk(List<String> tokens, PushMessage message) {
        BatchResponse response;
        try {
            response = firebaseMessaging.sendEachForMulticast(toMulticastMessage(tokens, message));
        } catch (FirebaseMessagingException e) {
            PushSendStatus status = FcmErrorClassifier.classify(e);
            logFailure(status, tokens.size(), e);
            return PushSendResult.of(tokens, status);
        } catch (RuntimeException e) {
            // data의 null 값처럼 메시지 구성 중 나는 예외도 호출 측 본 흐름을 깨지 않도록 발송 실패로 돌려준다
            log.error("FCM 메시지 구성·발송 중 예외: tokenCount={}", tokens.size(), e);
            return PushSendResult.of(tokens, PushSendStatus.FAILED);
        }
        PushSendResult result = toResult(tokens, response.getResponses());
        logFailures(result.outcomes(), response.getResponses());
        return result;
    }

    // FCM이 등록 토큰에서 FID(Firebase Installation ID)로 전환 중이라 addAllTokens가 deprecated다(firebase-admin 9.10.0~).
    // 종료일 공지 전까지는 완전히 지원되고, 전환 기간에는 tokens 필드가 FID도 받으므로 클라이언트 전환과 무관하게 그대로 쓴다.
    // 종료일이 공지되면 addAllFids로 바꾼다
    @SuppressWarnings("deprecation")
    private MulticastMessage toMulticastMessage(List<String> tokens, PushMessage message) {
        MulticastMessage.Builder builder = MulticastMessage.builder()
            .addAllTokens(tokens)
            .setNotification(Notification.builder()
                .setTitle(message.title())
                .setBody(message.body())
                .build());
        if (message.data() != null) {
            builder.putAllData(message.data());
        }
        return builder.build();
    }

    // BatchResponse의 응답 순서는 요청 토큰 순서와 같다
    private PushSendResult toResult(List<String> tokens, List<SendResponse> responses) {
        List<PushSendOutcome> outcomes = new ArrayList<>(tokens.size());
        for (int i = 0; i < responses.size(); i++) {
            SendResponse response = responses.get(i);
            PushSendStatus status = response.isSuccessful()
                ? PushSendStatus.SUCCESS
                : FcmErrorClassifier.classify(response.getException());
            outcomes.add(new PushSendOutcome(tokens.get(i), status));
        }
        return new PushSendResult(outcomes);
    }

    // 자격증명 오류처럼 토큰과 무관한 실패도 예외가 아니라 토큰별 실패로 돌아오므로, 묶음 안에서 상태별로 한 번씩 남긴다
    private void logFailures(List<PushSendOutcome> outcomes, List<SendResponse> responses) {
        Map<PushSendStatus, Integer> counts = new EnumMap<>(PushSendStatus.class);
        Map<PushSendStatus, FirebaseMessagingException> samples = new EnumMap<>(PushSendStatus.class);
        for (int i = 0; i < outcomes.size(); i++) {
            PushSendStatus status = outcomes.get(i).status();
            if (status != PushSendStatus.SUCCESS) {
                counts.merge(status, 1, Integer::sum);
                samples.putIfAbsent(status, responses.get(i).getException());
            }
        }
        counts.forEach((status, count) -> logFailure(status, count, samples.get(status)));
    }

    // 설정 문제(FAILED)는 사람이 고쳐야 하므로 ERROR, 일시적 장애(RETRYABLE)는 WARN으로 남긴다.
    // INVALID_TOKEN은 정상적인 토큰 만료이고 호출 측이 정리하므로 남기지 않는다
    private void logFailure(PushSendStatus status, int tokenCount, FirebaseMessagingException e) {
        if (status == PushSendStatus.FAILED) {
            log.error("FCM 발송 실패(설정 확인 필요): tokenCount={}, errorCode={}", tokenCount, e.getErrorCode(), e);
        } else if (status == PushSendStatus.RETRYABLE) {
            log.warn("FCM 발송 일시 실패(재시도 가능): tokenCount={}, errorCode={}", tokenCount, e.getErrorCode(), e);
        }
    }
}
