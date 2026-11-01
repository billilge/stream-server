package kr.ac.kookmin.stream.internal.domain.display.domain;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DisplayCalendarSchedule {

    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;

    public static DisplayCalendarSchedule of(Long id, String title, LocalDate startDate, LocalDate endDate) {
        return new DisplayCalendarSchedule(id, title, startDate, endDate);
    }
}
