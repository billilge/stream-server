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

    /**
     * 새 대여 이력을 만든다. workerId가 null이면 학생 본인 신청으로 승인 대기(PENDING) 상태에서 시작하고,
     * workerId가 있으면 관리자 대행 등록으로 물품 종류에 따라 RENTAL 또는 RETURNED로 바로 시작한다.
     */
    public static RentalHistory create(
        Item item, Long memberId, Long workerId, int rentedCount, LocalDateTime appliedAt, LocalDateTime rentAt
    ) {
        if (workerId == null) {
            return new RentalHistory(null, item.getId(), memberId, null, null, RentalStatus.PENDING, rentedCount, appliedAt, rentAt, null);
        }
        boolean returnable = item.getType() == ItemType.RENTAL;
        RentalStatus status = returnable ? RentalStatus.RENTAL : RentalStatus.RETURNED;
        LocalDateTime returnedAt = returnable ? null : appliedAt;
        return new RentalHistory(null, item.getId(), memberId, workerId, null, status, rentedCount, appliedAt, rentAt, returnedAt);
    }

    /** 반납 신청 처리. 운영진이 실제 반납을 확인해 줘야 하므로 신청 즉시 완료가 아니라 반납 대기(RETURN_PENDING)로 전이한다. */
    public void requestReturn() {
        this.rentalStatus = RentalStatus.RETURN_PENDING;
    }

    /** 본인이 PENDING 상태로 걸어 둔 신청을 취소한다. 아직 운영진이 승인하기 전이라 재고를 그대로 되돌릴 수 있다. */
    public void cancel() {
        this.rentalStatus = RentalStatus.CANCEL;
    }

    /** 운영진이 상태를 직접 바꾼다. RENTAL로 바뀌면 지금을 수령 시각으로, RETURNED로 바뀌면 지금을 반납 완료 시각으로 기록한다. */
    public void changeStatusByAdmin(RentalStatus newStatus, LocalDateTime now) {
        if (newStatus == RentalStatus.RENTAL) {
            this.rentAt = now;
        }
        if (newStatus == RentalStatus.RETURNED) {
            this.returnedAt = now;
        }
        this.rentalStatus = newStatus;
    }

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
