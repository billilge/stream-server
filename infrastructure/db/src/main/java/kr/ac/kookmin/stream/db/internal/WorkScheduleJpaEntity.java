package kr.ac.kookmin.stream.db.internal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import kr.ac.kookmin.stream.db.common.BaseTimeEntity;
import kr.ac.kookmin.stream.internal.domain.schedule.domain.WorkSchedule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "work_schedules",
    indexes = {
        @Index(name = "idx_work_schedules_member_id", columnList = "member_id")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkScheduleJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "day_of_week", nullable = false, length = 10)
    private String dayOfWeek;

    @Column(name = "start_hour", nullable = false, columnDefinition = "TINYINT")
    private int startHour;

    @Column(name = "end_hour", nullable = false, columnDefinition = "TINYINT")
    private int endHour;

    private WorkScheduleJpaEntity(WorkSchedule schedule) {
        this.id = schedule.getId();
        this.memberId = schedule.getMemberId();
        this.dayOfWeek = schedule.getDayOfWeek();
        this.startHour = schedule.getStartHour();
        this.endHour = schedule.getEndHour();
    }

    public static WorkScheduleJpaEntity from(WorkSchedule schedule) {
        return new WorkScheduleJpaEntity(schedule);
    }

    public WorkSchedule toDomain() {
        return WorkSchedule.of(id, memberId, dayOfWeek, startHour, endHour);
    }
}
