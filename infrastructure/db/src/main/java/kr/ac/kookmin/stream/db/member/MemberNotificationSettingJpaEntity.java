package kr.ac.kookmin.stream.db.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import kr.ac.kookmin.stream.db.common.BaseTimeEntity;
import kr.ac.kookmin.stream.member.domain.notification.domain.MemberNotificationSetting;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "member_notification_settings",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_member_notification_settings_member_id", columnNames = {"member_id"})
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberNotificationSettingJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_notification_setting_id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "rental_enabled", nullable = false)
    private boolean rentalEnabled;

    @Column(name = "event_enabled", nullable = false)
    private boolean eventEnabled;

    @Column(name = "locker_enabled", nullable = false)
    private boolean lockerEnabled;

    @Column(name = "notice_enabled", nullable = false)
    private boolean noticeEnabled;

    private MemberNotificationSettingJpaEntity(MemberNotificationSetting setting) {
        this.id = setting.getId();
        this.memberId = setting.getMemberId();
        this.rentalEnabled = setting.isRentalEnabled();
        this.eventEnabled = setting.isEventEnabled();
        this.lockerEnabled = setting.isLockerEnabled();
        this.noticeEnabled = setting.isNoticeEnabled();
    }

    public static MemberNotificationSettingJpaEntity from(MemberNotificationSetting setting) {
        return new MemberNotificationSettingJpaEntity(setting);
    }

    public MemberNotificationSetting toDomain() {
        return MemberNotificationSetting.of(id, memberId, rentalEnabled, eventEnabled, lockerEnabled, noticeEnabled);
    }
}
