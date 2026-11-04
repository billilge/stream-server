package kr.ac.kookmin.stream.member.domain.notification.repository;

import java.util.Collection;
import java.util.List;
import kr.ac.kookmin.stream.member.domain.notification.domain.PushTarget;

/**
 * 알림 수신자 조회. 알림함 대상은 활성 회원이고, 푸시 대상은 그중 토큰과 알림 설정이 있는 회원이다.
 * FCM이 무효라고 응답한 토큰 정리도 여기서 한다.
 */
public interface NotificationRecipientRepository {
    List<Long> findAllActiveMemberIds();
    List<PushTarget> findPushTargets(Collection<Long> memberIds);
    List<PushTarget> findAllPushTargets();
    /** 운영진(Role.ADMIN) 전체의 푸시 대상. 관리자 공용 알림을 보낼 때 쓴다. */
    List<PushTarget> findAllAdminPushTargets();
    int clearFcmTokens(Collection<String> fcmTokens);
}
