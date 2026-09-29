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
    /** 운영진이 학생에게 공개했는지. 미게시 회차는 학생에게 없는 것으로 보여야 한다. */
    private boolean published;

    public static LockerPeriod of(
        Long id,
        String name,
        LocalDateTime applyStartAt,
        LocalDateTime applyEndAt,
        LocalDate usageStartAt,
        LocalDate usageEndAt,
        boolean published
    ) {
        return new LockerPeriod(id, name, applyStartAt, applyEndAt, usageStartAt, usageEndAt, published);
    }
}
