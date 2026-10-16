package kr.ac.kookmin.stream.event.domain.locker.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import kr.ac.kookmin.stream.common.BusinessException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LockerPeriod {

    private static final int FIRST_SEMESTER = 1;
    private static final int SECOND_SEMESTER = 2;

    private Long id;
    private int year;
    private int semester;
    /** 연도와 학기로 만든 표시용 이름. 예: 2026-1학기 */
    private String name;
    private LocalDateTime applyStartAt;
    private LocalDateTime applyEndAt;
    private LocalDate usageStartAt;
    private LocalDate usageEndAt;
    /** 운영진이 학생에게 공개했는지. 미게시 회차는 학생에게 없는 것으로 보여야 한다. */
    private boolean published;

    public static LockerPeriod of(
        Long id,
        int year,
        int semester,
        String name,
        LocalDateTime applyStartAt,
        LocalDateTime applyEndAt,
        LocalDate usageStartAt,
        LocalDate usageEndAt,
        boolean published
    ) {
        return new LockerPeriod(id, year, semester, name, applyStartAt, applyEndAt, usageStartAt, usageEndAt, published);
    }

    /**
     * 운영진이 새 회차를 만든다. 이름은 연도와 학기로 서버가 만든다.
     * <p>
     * 학생에게 회차를 보여주는 경로는 홈 화면 배너뿐이고 그쪽에서 따로 설정하므로, 게시 여부는 요청으로 받지 않고 바로 게시 상태로 둔다.
     * 미게시로 두면 구역 조회와 신청이 모두 회차를 찾지 못한다.
     */
    public static LockerPeriod create(LockerPeriodCreateCommand command) {
        validateSemester(command.semester());
        validateDates(command.applyStartDate(), command.applyEndDate(), command.usageStartDate(), command.usageEndDate());

        return new LockerPeriod(
            null,
            command.year(),
            command.semester(),
            toName(command.year(), command.semester()),
            command.applyStartDate().atStartOfDay(),
            command.applyEndDate().atTime(LocalTime.MAX.withNano(0)),
            command.usageStartDate(),
            command.usageEndDate(),
            true
        );
    }

    /**
     * 신청·사용 기간을 바꾼다. 연도·학기와 게시 여부는 이 경로로 바뀌지 않는다.
     *
     * @param now 회차 상태를 판단할 기준 시각
     */
    public void update(LockerPeriodUpdateCommand command, LocalDateTime now) {
        validateDates(command.applyStartDate(), command.applyEndDate(), command.usageStartDate(), command.usageEndDate());
        validateChangeable(command.applyStartDate(), now);

        this.applyStartAt = command.applyStartDate().atStartOfDay();
        this.applyEndAt = command.applyEndDate().atTime(LocalTime.MAX.withNano(0));
        this.usageStartAt = command.usageStartDate();
        this.usageEndAt = command.usageEndDate();
    }

    /**
     * 신청 시작 전이면 예정, 사용 종료일이 지났으면 종료, 그 사이는 진행 중이다.
     */
    public LockerPeriodStatus calculateStatus(LocalDateTime now) {
        if (now.isBefore(applyStartAt)) {
            return LockerPeriodStatus.UPCOMING;
        }
        if (isUsageEnded(now.toLocalDate())) {
            return LockerPeriodStatus.ENDED;
        }
        return LockerPeriodStatus.IN_PROGRESS;
    }

    /**
     * 회차 범위(신청 시작일 ~ 사용 종료일)가 다른 회차와 겹치는지. 회차는 같은 시기에 둘이 돌 수 없다.
     */
    public boolean overlaps(LockerPeriod other) {
        return !applyStartAt.toLocalDate().isAfter(other.usageEndAt)
            && !other.applyStartAt.toLocalDate().isAfter(usageEndAt);
    }

    /**
     * 사용 기간이 끝났는지. 사용 종료일 당일까지는 사용 중으로 본다.
     */
    public boolean isUsageEnded(LocalDate today) {
        return today.isAfter(usageEndAt);
    }

    private static String toName(int year, int semester) {
        return "%d-%d학기".formatted(year, semester);
    }

    private static void validateSemester(int semester) {
        if (semester != FIRST_SEMESTER && semester != SECOND_SEMESTER) {
            throw new BusinessException(LockerErrorCode.INVALID_LOCKER_PERIOD_SEMESTER);
        }
    }

    /**
     * 신청 마감일과 사용 시작일의 선후는 보지 않는다. 사용을 시작한 뒤 남은 자리를 추가 모집하려고 마감일을 늘리는
     * 운영이 있어 두 기간이 겹칠 수 있다.
     */
    private static void validateDates(
        LocalDate applyStartDate,
        LocalDate applyEndDate,
        LocalDate usageStartDate,
        LocalDate usageEndDate
    ) {
        if (applyStartDate.isAfter(applyEndDate) || usageStartDate.isAfter(usageEndDate)) {
            throw new BusinessException(LockerErrorCode.INVALID_LOCKER_PERIOD_RANGE);
        }
    }

    /**
     * 종료된 회차는 손대지 않는다. 진행 중인 회차는 이미 학생이 신청을 시작했으므로 신청 시작일을 되돌릴 수 없다.
     * <p>
     * 수정이 전체 교체라 바뀌지 않은 시작일도 원래 값 그대로 다시 올라온다. 그래서 "보냈는가"가 아니라 "원래와 다른가"를 본다.
     */
    private void validateChangeable(LocalDate applyStartDate, LocalDateTime now) {
        LockerPeriodStatus status = calculateStatus(now);
        if (status == LockerPeriodStatus.ENDED) {
            throw new BusinessException(LockerErrorCode.LOCKER_PERIOD_ALREADY_ENDED);
        }
        if (status == LockerPeriodStatus.IN_PROGRESS && !applyStartDate.equals(applyStartAt.toLocalDate())) {
            throw new BusinessException(LockerErrorCode.LOCKER_PERIOD_APPLY_ALREADY_STARTED);
        }
    }
}
