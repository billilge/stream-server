package kr.ac.kookmin.stream.member.domain.notification.service.impl;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kr.ac.kookmin.stream.member.domain.notification.client.PushNotificationClient;
import kr.ac.kookmin.stream.member.domain.notification.domain.Notification;
import kr.ac.kookmin.stream.member.domain.notification.domain.NotificationType;
import kr.ac.kookmin.stream.member.domain.notification.domain.PushMessage;
import kr.ac.kookmin.stream.member.domain.notification.domain.PushSendResult;
import kr.ac.kookmin.stream.member.domain.notification.domain.PushSendStatus;
import kr.ac.kookmin.stream.member.domain.notification.domain.PushTarget;
import kr.ac.kookmin.stream.member.domain.notification.repository.NotificationRecipientRepository;
import kr.ac.kookmin.stream.member.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 알림 한 건을 수신자에게 보낸다. 알림함에는 수신자 전원에게 저장하고, 푸시는 그 카테고리를 켜 두고 토큰이 있는 회원에게만 보낸다.
 * <p>
 * 아웃박스 릴레이가 부르는 리스너에서 트랜잭션 없이 호출된다. 알림함 저장은 {@code saveAll}의 트랜잭션으로 한꺼번에 반영하고,
 * 저장이 끝난 뒤 트랜잭션 밖에서 푸시를 보낸다. 알림함 저장이 실패하면 예외가 리스너까지 올라가 아웃박스가 다시 발행한다.
 */
@Component
@RequiredArgsConstructor
class NotificationDispatcher {

    private static final Logger log = LoggerFactory.getLogger(NotificationDispatcher.class);

    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;
    private final PushNotificationClient pushNotificationClient;

    /** 지정한 회원에게 보낸다. */
    void dispatch(NotificationType type, List<String> formatValues, long targetId, Collection<Long> memberIds) {
        List<Long> recipients = List.copyOf(new LinkedHashSet<>(memberIds));
        List<PushTarget> pushTargets = notificationRecipientRepository.findPushTargets(recipients);
        send(type, formatValues, targetId, recipients, pushTargets);
    }

    /** 활성 회원 전체에서 제외할 회원(예: 이미 신청한 회원)을 뺀 나머지에게 보낸다. */
    void dispatchToAll(NotificationType type, List<String> formatValues, long targetId, Collection<Long> excludedMemberIds) {
        Set<Long> excluded = Set.copyOf(excludedMemberIds);
        List<Long> recipients = notificationRecipientRepository.findAllActiveMemberIds().stream()
            .filter(memberId -> !excluded.contains(memberId))
            .toList();
        List<PushTarget> pushTargets = notificationRecipientRepository.findAllPushTargets().stream()
            .filter(target -> !excluded.contains(target.memberId()))
            .toList();
        send(type, formatValues, targetId, recipients, pushTargets);
    }

    private void send(
        NotificationType type,
        List<String> formatValues,
        long targetId,
        List<Long> recipients,
        List<PushTarget> pushTargets
    ) {
        if (recipients.isEmpty()) {
            return;
        }
        notificationRepository.saveAll(recipients.stream()
            .map(memberId -> Notification.create(memberId, type, formatValues, targetId))
            .toList());

        // 한 기기의 토큰이 여러 회원 행에 남아 있을 수 있어 토큰 단위로 중복을 지운다. 그대로 보내면 같은 기기에 여러 번 간다
        List<String> tokens = pushTargets.stream()
            .filter(target -> target.accepts(type.getCategory()))
            .map(PushTarget::fcmToken)
            .distinct()
            .toList();
        int invalidTokenCount = 0;
        if (!tokens.isEmpty()) {
            PushSendResult result = pushNotificationClient.send(tokens, toPushMessage(type, formatValues, targetId));
            invalidTokenCount = clearInvalidTokens(result.tokensWith(PushSendStatus.INVALID_TOKEN));
        }
        log.info("알림 발송: type={}, targetId={}, recipientCount={}, pushTokenCount={}, invalidTokenCount={}",
            type, targetId, recipients.size(), tokens.size(), invalidTokenCount);
    }

    private PushMessage toPushMessage(NotificationType type, List<String> formatValues, long targetId) {
        Map<String, String> data = Map.of(
            "type", type.name(),
            "category", type.getCategory().name(),
            "targetId", String.valueOf(targetId)
        );
        return new PushMessage(type.formatTitle(formatValues), type.formatBody(formatValues), data);
    }

    // 푸시는 이미 나갔으므로 정리가 실패해도 예외를 던지지 않는다. 던지면 아웃박스가 다시 발행해 같은 알림이 한 번 더 간다
    private int clearInvalidTokens(List<String> invalidTokens) {
        if (invalidTokens.isEmpty()) {
            return 0;
        }
        try {
            notificationRecipientRepository.clearFcmTokens(invalidTokens);
        } catch (RuntimeException e) {
            log.warn("무효 FCM 토큰 정리 실패: tokenCount={}", invalidTokens.size(), e);
        }
        return invalidTokens.size();
    }
}
