package kr.ac.kookmin.stream.welfare.domain.rental.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RentalStatusWorkerLog {

    private Long id;
    private Long rentalHistoryId;
    private Long workerId;
    private RentalStatus rentalStatus;

    public static RentalStatusWorkerLog of(
        Long id,
        Long rentalHistoryId,
        Long workerId,
        RentalStatus rentalStatus
    ) {
        return new RentalStatusWorkerLog(id, rentalHistoryId, workerId, rentalStatus);
    }
}
