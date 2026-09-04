package kr.ac.kookmin.stream.event.domain.locker.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LockerPeriod {

    private Long id;
    private String name;
    private LocalDateTime applyStartAt;
    private LocalDateTime applyEndAt;
    private LocalDate usageStartAt;
    private LocalDate usageEndAt;

    public static LockerPeriod of(
        Long id,
        String name,
        LocalDateTime applyStartAt,
        LocalDateTime applyEndAt,
        LocalDate usageStartAt,
        LocalDate usageEndAt
    ) {
        return new LockerPeriod(id, name, applyStartAt, applyEndAt, usageStartAt, usageEndAt);
    }
}
