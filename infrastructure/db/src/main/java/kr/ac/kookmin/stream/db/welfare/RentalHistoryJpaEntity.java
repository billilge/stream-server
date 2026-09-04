package kr.ac.kookmin.stream.db.welfare;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import kr.ac.kookmin.stream.db.common.BaseTimeEntity;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "rental_histories",
    indexes = {
        @Index(name = "idx_rental_histories_member_id", columnList = "member_id"),
        @Index(name = "idx_rental_histories_item_id", columnList = "item_id")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentalHistoryJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "worker_id")
    private Long workerId;

    @Column(name = "item_code")
    private String itemCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "rental_status", nullable = false, length = 30)
    private RentalStatus rentalStatus;

    @Column(name = "rented_count", nullable = false)
    private int rentedCount;

    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt;

    @Column(name = "rent_at")
    private LocalDateTime rentAt;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    private RentalHistoryJpaEntity(RentalHistory history) {
        this.id = history.getId();
        this.itemId = history.getItemId();
        this.memberId = history.getMemberId();
        this.workerId = history.getWorkerId();
        this.itemCode = history.getItemCode();
        this.rentalStatus = history.getRentalStatus();
        this.rentedCount = history.getRentedCount();
        this.appliedAt = history.getAppliedAt();
        this.rentAt = history.getRentAt();
        this.returnedAt = history.getReturnedAt();
    }

    public static RentalHistoryJpaEntity from(RentalHistory history) {
        return new RentalHistoryJpaEntity(history);
    }

    public RentalHistory toDomain() {
        return RentalHistory.of(id, itemId, memberId, workerId, itemCode, rentalStatus,
            rentedCount, appliedAt, rentAt, returnedAt);
    }
}
