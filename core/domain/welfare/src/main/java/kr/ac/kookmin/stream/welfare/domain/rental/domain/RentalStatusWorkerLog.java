package kr.ac.kookmin.stream.welfare.domain.rental.domain;

import java.time.LocalDateTime;
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
    private LocalDateTime createdAt;

    /** 처리자 로그를 새로 남긴다. 저장 전이라 식별자·생성 시각은 아직 없다. */
    public static RentalStatusWorkerLog create(Long rentalHistoryId, Long workerId, RentalStatus rentalStatus) {
        return new RentalStatusWorkerLog(null, rentalHistoryId, workerId, rentalStatus, null);
    }

    public static RentalStatusWorkerLog of(
        Long id,
        Long rentalHistoryId,
        Long workerId,
        RentalStatus rentalStatus,
        LocalDateTime createdAt
    ) {
        return new RentalStatusWorkerLog(id, rentalHistoryId, workerId, rentalStatus, createdAt);
    }
}
