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
     * 대여 신청으로 새 이력을 만든다. 물품 종류와 관계없이 승인 대기(PENDING)로 시작한다 — 운영진이
     * {@link #changeStatusByAdmin}으로 승인해야 다음 단계로 넘어간다(PR #75가 남겨둔 "신청 즉시 확정" 전제를
     * 이 PR에서 되돌린다).
     */
    public static RentalHistory create(Item item, Long memberId, int rentedCount, LocalDateTime appliedAt, LocalDateTime rentAt) {
        return new RentalHistory(null, item.getId(), memberId, null, null, RentalStatus.PENDING, rentedCount, appliedAt, rentAt, null);
    }

    /**
     * 관리자가 현장에서 처리한 대여를 대신 등록한다. 이미 물건을 건네준 걸 사후 기록하는 용도라 상태 선택을
     * 받지 않는다 — {@link #create}와 같은 기준으로 대여품은 RENTAL, 소모품은 RETURNED로 바로 시작한다.
     * 처리자(workerId)는 호출한 관리자가 아니라 폼에서 직접 고른 근무자다.
     */
    public static RentalHistory createByAdmin(
        Item item, Long memberId, Long workerId, int rentedCount, LocalDateTime appliedAt, LocalDateTime rentAt
    ) {
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
    public void cancelByMember() {
        this.rentalStatus = RentalStatus.CANCEL;
    }

    /** 반납 기한을 넘긴 대여를 연체로 표시한다({@code RentalNotificationScheduler}가 매일 호출). */
    public void markOverdue() {
        this.rentalStatus = RentalStatus.OVERDUE;
    }

    /**
     * 운영진이 상태를 직접 바꾼다. 전이 규칙은 없다(어떤 값이든 허용). RENTAL로 바뀌는데 아직 실제 수령
     * 시각이 없으면 지금을 수령 시각으로 기록하고, RETURNED로 바뀌는데 아직 반납 완료 시각이 없으면 지금을
     * 반납 완료 시각으로 기록한다.
     */
    public void changeStatusByAdmin(RentalStatus newStatus, LocalDateTime now) {
        if (newStatus == RentalStatus.RENTAL && this.rentAt == null) {
            this.rentAt = now;
        }
        if (newStatus == RentalStatus.RETURNED && this.returnedAt == null) {
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
