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
import kr.ac.kookmin.stream.db.common.BaseCreatedTimeEntity;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatusWorkerLog;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "rental_status_worker_logs",
    indexes = {
        @Index(name = "idx_rental_status_worker_logs_rental_history_id", columnList = "rental_history_id")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentalStatusWorkerLogJpaEntity extends BaseCreatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rental_status_worker_log_id")
    private Long id;

    @Column(name = "rental_history_id", nullable = false)
    private Long rentalHistoryId;

    @Column(name = "worker_id", nullable = false)
    private Long workerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "rental_status", nullable = false, length = 30)
    private RentalStatus rentalStatus;

    private RentalStatusWorkerLogJpaEntity(RentalStatusWorkerLog log) {
        this.id = log.getId();
        this.rentalHistoryId = log.getRentalHistoryId();
        this.workerId = log.getWorkerId();
        this.rentalStatus = log.getRentalStatus();
    }

    public static RentalStatusWorkerLogJpaEntity from(RentalStatusWorkerLog log) {
        return new RentalStatusWorkerLogJpaEntity(log);
    }

    public RentalStatusWorkerLog toDomain() {
        return RentalStatusWorkerLog.of(id, rentalHistoryId, workerId, rentalStatus);
    }
}
