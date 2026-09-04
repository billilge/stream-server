package kr.ac.kookmin.stream.internal.domain.schedule.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkSchedule {

    private Long id;
    private Long memberId;
    private String dayOfWeek;
    private int startHour;
    private int endHour;

    public static WorkSchedule of(
        Long id,
        Long memberId,
        String dayOfWeek,
        int startHour,
        int endHour
    ) {
        return new WorkSchedule(id, memberId, dayOfWeek, startHour, endHour);
    }
}
