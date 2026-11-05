package kr.ac.kookmin.stream.member.domain.notification.repository;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.member.domain.notification.domain.Notification;
import kr.ac.kookmin.stream.member.domain.notification.domain.NotificationCursor;

public interface NotificationRepository {
    void saveAll(List<Notification> notifications);

    /** 회원 개인 알림함. member_id가 이 회원인 행만 조회한다. */
    CursorSliceResult<Notification> findSliceByMemberId(Long memberId, NotificationCursor cursor, int size);

    /** 운영진 공용 알림함. member_id가 없는 행만 조회한다. */
    CursorSliceResult<Notification> findAdminSlice(NotificationCursor cursor, int size);

    /** 회원 개인 알림 중 안 읽은 알림이 하나라도 있는지 본다. */
    boolean existsUnreadByMemberId(Long memberId);

    /** 운영진 공용 알림 중 안 읽은 알림이 하나라도 있는지 본다. */
    boolean existsAdminUnread();

    Optional<Notification> findById(Long id);

    Notification save(Notification notification);
}
