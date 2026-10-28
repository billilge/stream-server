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
     * 대여 신청으로 새 이력을 만든다. 소모품(반납 개념이 없는 물품)은 처음부터 반납 완료로,
     * 대여품은 대여 중으로 시작한다 — 이 프로젝트엔 운영진이 수령을 확정해 주는 단계가 없어서,
     * 레거시(billilge/backend) {@code RentalService.updateRentalStatus()}가 물품 인도 시점에 하던
     * "소모품이면 RENTAL 대신 RETURNED" 분기를 신청 시점으로 그대로 옮겼다.
     */
    public static RentalHistory create(Item item, Long memberId, int rentedCount, LocalDateTime appliedAt, LocalDateTime rentAt) {
        boolean returnable = item.getType() == ItemType.RENTAL;
        RentalStatus status = returnable ? RentalStatus.RENTAL : RentalStatus.RETURNED;
        LocalDateTime returnedAt = returnable ? null : appliedAt;
        return new RentalHistory(null, item.getId(), memberId, null, null, status, rentedCount, appliedAt, rentAt, returnedAt);
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

    /** 반납 신청 처리. 운영진의 최종 확인 단계가 없어 신청 즉시 반납 완료로 전이한다. */
    public void markReturned(LocalDateTime returnedAt) {
        this.rentalStatus = RentalStatus.RETURNED;
        this.returnedAt = returnedAt;
    }

    /** 반납 기한을 넘긴 대여를 연체로 표시한다({@code RentalNotificationScheduler}가 매일 호출). */
    public void markOverdue() {
        this.rentalStatus = RentalStatus.OVERDUE;
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
