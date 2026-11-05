package kr.ac.kookmin.stream.db.member;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.member.domain.notification.domain.Notification;
import kr.ac.kookmin.stream.member.domain.notification.domain.NotificationCursor;
import kr.ac.kookmin.stream.member.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationJpaRepository notificationJpaRepository;

    @Override
    public void saveAll(List<Notification> notifications) {
        notificationJpaRepository.saveAll(notifications.stream()
            .map(NotificationJpaEntity::from)
            .toList());
    }

    @Override
    public CursorSliceResult<Notification> findSliceByMemberId(Long memberId, NotificationCursor cursor, int size) {
        Pageable pageable = Pageable.ofSize(size + 1);
        List<NotificationJpaEntity> entities = notificationJpaRepository.findSliceByMemberId(
            memberId,
            cursor == null ? null : cursor.createdAt(),
            cursor == null ? null : cursor.id(),
            pageable
        );
        return CursorSliceResult.ofSlice(
            entities, size, NotificationJpaEntity::toDomain, notification -> NotificationCursor.of(notification).format());
    }

    @Override
    public CursorSliceResult<Notification> findAdminSlice(NotificationCursor cursor, int size) {
        Pageable pageable = Pageable.ofSize(size + 1);
        List<NotificationJpaEntity> entities = notificationJpaRepository.findAdminSlice(
            cursor == null ? null : cursor.createdAt(),
            cursor == null ? null : cursor.id(),
            pageable
        );
        return CursorSliceResult.ofSlice(
            entities, size, NotificationJpaEntity::toDomain, notification -> NotificationCursor.of(notification).format());
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return notificationJpaRepository.findById(id).map(NotificationJpaEntity::toDomain);
    }

    @Override
    public Notification save(Notification notification) {
        return notificationJpaRepository.save(NotificationJpaEntity.from(notification)).toDomain();
    }
}
