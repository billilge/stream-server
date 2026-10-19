package kr.ac.kookmin.stream.db.member;

import java.util.List;
import kr.ac.kookmin.stream.member.domain.notification.domain.Notification;
import kr.ac.kookmin.stream.member.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
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
}
