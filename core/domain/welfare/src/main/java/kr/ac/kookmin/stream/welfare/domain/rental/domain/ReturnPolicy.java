package kr.ac.kookmin.stream.welfare.domain.rental.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 대여품의 반납 정책.
 *
 * @param maxRentalDays  대여일부터 최대 대여 가능 일수. 0이면 당일 반납
 * @param returnDeadline 반납 마감 시각
 */
public record ReturnPolicy(int maxRentalDays, LocalTime returnDeadline) {

    private static final LocalTime FIXED_RETURN_DEADLINE = LocalTime.of(17, 0);

    /** 반납 마감 시각은 운영진이 고르지 않고 항상 17:00으로 고정한다. */
    public static ReturnPolicy of(int maxRentalDays) {
        return new ReturnPolicy(maxRentalDays, FIXED_RETURN_DEADLINE);
    }

    /**
     * 대여 시각으로부터 반납 기한 시각을 계산한다. 대여한 날짜에 최대 대여 일수를 더한 날의 마감 시각이다.
     */
    public LocalDateTime dueAt(LocalDateTime rentAt) {
        return rentAt.toLocalDate().plusDays(maxRentalDays).atTime(returnDeadline);
    }
}
