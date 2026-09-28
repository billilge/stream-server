package kr.ac.kookmin.stream.client.push.fcm;

import com.google.firebase.ErrorCode;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import java.net.SocketException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import kr.ac.kookmin.stream.member.domain.notification.domain.PushSendStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * FCM 발송 예외를 토큰별 결과 상태로 분류한다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class FcmErrorClassifier {

    // FCM 에러 코드가 없는 실패(타임아웃·호스트 조회 실패 등 네트워크 오류)의 일시적 장애 판정 기준
    private static final Set<ErrorCode> TRANSIENT_PLATFORM_ERRORS =
        EnumSet.of(ErrorCode.UNAVAILABLE, ErrorCode.INTERNAL, ErrorCode.DEADLINE_EXCEEDED);

    static PushSendStatus classify(FirebaseMessagingException e) {
        MessagingErrorCode code = e.getMessagingErrorCode();
        if (code == null) {
            return isTransientNetworkError(e) ? PushSendStatus.RETRYABLE : PushSendStatus.FAILED;
        }
        // default를 두지 않는다. SDK에 새 에러 코드가 생기면 업그레이드 시 컴파일 에러로 분류 누락이 드러난다
        return switch (code) {
            // FCM이 무효 토큰 신호로 안내하는 에러. INVALID_ARGUMENT는 페이로드 오류일 때도 나므로 정리 시 유의한다
            case UNREGISTERED, INVALID_ARGUMENT -> PushSendStatus.INVALID_TOKEN;
            // UNAVAILABLE(503)은 SDK가 이미 최대 4회 재시도하고도 남은 실패다
            case UNAVAILABLE, INTERNAL, QUOTA_EXCEEDED -> PushSendStatus.RETRYABLE;
            // SENDER_ID_MISMATCH는 서비스 계정이 다른 프로젝트를 가리켜도 전 토큰에 나므로, 토큰 정리 대상으로 보지 않는다
            case THIRD_PARTY_AUTH_ERROR, SENDER_ID_MISMATCH -> PushSendStatus.FAILED;
        };
    }

    // 연결 거부·재설정(ConnectException 등)은 SDK가 자격증명 오류와 같은 UNKNOWN으로 내리므로 원인 예외로 가린다
    private static boolean isTransientNetworkError(FirebaseMessagingException e) {
        return TRANSIENT_PLATFORM_ERRORS.contains(e.getErrorCode()) || causedBy(e, SocketException.class);
    }

    private static boolean causedBy(Throwable e, Class<? extends Throwable> type) {
        Set<Throwable> visited = new HashSet<>();
        for (Throwable cause = e; cause != null && visited.add(cause); cause = cause.getCause()) {
            if (type.isInstance(cause)) {
                return true;
            }
        }
        return false;
    }
}
