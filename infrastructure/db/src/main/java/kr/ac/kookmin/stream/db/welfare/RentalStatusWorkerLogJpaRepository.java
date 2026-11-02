package kr.ac.kookmin.stream.db.welfare;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalStatusWorkerLogJpaRepository extends JpaRepository<RentalStatusWorkerLogJpaEntity, Long> {

    List<RentalStatusWorkerLogJpaEntity> findAllByRentalHistoryIdOrderByCreatedAtDesc(Long rentalHistoryId);
}
