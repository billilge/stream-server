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

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    private DisplayCalendarScheduleJpaEntity(DisplayCalendarSchedule schedule) {
        this.id = schedule.getId();
        this.title = schedule.getTitle();
        this.startDate = schedule.getStartDate();
        this.endDate = schedule.getEndDate();
    }

    public static DisplayCalendarScheduleJpaEntity from(DisplayCalendarSchedule schedule) {
        return new DisplayCalendarScheduleJpaEntity(schedule);
    }

    public DisplayCalendarSchedule toDomain() {
        return DisplayCalendarSchedule.of(id, title, startDate, endDate);
    }
}
