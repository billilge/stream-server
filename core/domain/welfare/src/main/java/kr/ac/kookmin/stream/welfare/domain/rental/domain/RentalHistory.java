package kr.ac.kookmin.stream.welfare.domain.rental.domain;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RentalHistory {

    private Long id;
    private Long itemId;
    private Long memberId;
    private Long workerId;
    private String itemCode;
    private RentalStatus rentalStatus;
    private int rentedCount;
    private LocalDateTime appliedAt;
    private LocalDateTime rentAt;
    private LocalDateTime returnedAt;

    public static RentalHistory of(
        Long id,
        Long itemId,
        Long memberId,
        Long workerId,
        String itemCode,
        RentalStatus rentalStatus,
        int rentedCount,
        LocalDateTime appliedAt,
        LocalDateTime rentAt,
        LocalDateTime returnedAt
    ) {
        return new RentalHistory(
            id, itemId, memberId, workerId, itemCode, rentalStatus, rentedCount, appliedAt, rentAt,
            returnedAt
        );
    }
}
