package kr.ac.kookmin.stream.db.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import kr.ac.kookmin.stream.db.common.BaseCreatedTimeEntity;
import kr.ac.kookmin.stream.member.domain.notification.domain.Notification;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "notifications",
    indexes = {
        @Index(name = "idx_notifications_member_id", columnList = "member_id")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationJpaEntity extends BaseCreatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(name = "format_values")
    private String formatValues;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    private NotificationJpaEntity(Notification notification) {
        this.id = notification.getId();
        this.memberId = notification.getMemberId();
        this.type = notification.getType();
        this.formatValues = notification.getFormatValues();
        this.read = notification.isRead();
    }

    public static NotificationJpaEntity from(Notification notification) {
        return new NotificationJpaEntity(notification);
    }

    public Notification toDomain() {
        return Notification.of(id, memberId, type, formatValues, read);
    }
}
