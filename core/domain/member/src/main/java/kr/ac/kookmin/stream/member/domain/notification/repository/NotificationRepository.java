package kr.ac.kookmin.stream.member.domain.notification.repository;

import java.util.List;
import kr.ac.kookmin.stream.member.domain.notification.domain.Notification;

public interface NotificationRepository {
    void saveAll(List<Notification> notifications);
}
