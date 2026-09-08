package kr.ac.kookmin.stream.db.internal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import kr.ac.kookmin.stream.db.common.BaseTimeEntity;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayCalendarSchedule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "display_calendar_schedules")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DisplayCalendarScheduleJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String schedules;

    private DisplayCalendarScheduleJpaEntity(DisplayCalendarSchedule schedule) {
        this.id = schedule.getId();
        this.date = schedule.getDate();
        this.schedules = schedule.getSchedules();
    }

    public static DisplayCalendarScheduleJpaEntity from(DisplayCalendarSchedule schedule) {
        return new DisplayCalendarScheduleJpaEntity(schedule);
    }

    public DisplayCalendarSchedule toDomain() {
        return DisplayCalendarSchedule.of(id, date, schedules);
    }
}
