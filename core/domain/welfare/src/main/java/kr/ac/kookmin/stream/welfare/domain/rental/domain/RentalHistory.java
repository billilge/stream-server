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

    // 물품을 받아 간 뒤의 상태인지. RENTAL부터는 rentAt이 희망 시각이 아니라 실제 수령 시각이다
    private boolean isPickedUp() {
        return RentalStatus.awaitingReturnStatuses().contains(rentalStatus) || rentalStatus == RentalStatus.RETURNED;
    }

    /** 물품 코드를 운영진이 직접 매긴다. null로 보내면 지운다. */
    public void updateItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    /**
     * 운영진이 상태를 직접 바꾼다. 아직 물품을 받아 가기 전 상태에서 RENTAL로 바뀌면 지금을 수령 시각으로, RETURNED로 바뀌면
     * 지금을 반납 완료 시각으로 기록한다. 이미 받아 간 뒤의 상태(반납 신청 반려 등)에서 RENTAL로 되돌릴 때는 수령 시각을
     * 그대로 둔다. 덮어쓰면 반납 기한이 그날부터 다시 계산된다.
     */
    public void changeStatusByAdmin(RentalStatus newStatus, LocalDateTime now) {
        if (newStatus == RentalStatus.RENTAL && !isPickedUp()) {
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
