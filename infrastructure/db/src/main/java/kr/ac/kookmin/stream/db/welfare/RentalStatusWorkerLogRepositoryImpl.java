package kr.ac.kookmin.stream.db.welfare;

import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatusWorkerLog;
import kr.ac.kookmin.stream.welfare.domain.rental.repository.RentalStatusWorkerLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RentalStatusWorkerLogRepositoryImpl implements RentalStatusWorkerLogRepository {

    private final RentalStatusWorkerLogJpaRepository rentalStatusWorkerLogJpaRepository;

    @Override
    public RentalStatusWorkerLog save(RentalStatusWorkerLog log) {
        return rentalStatusWorkerLogJpaRepository.save(RentalStatusWorkerLogJpaEntity.from(log)).toDomain();
    }

    @Override
    public List<RentalStatusWorkerLog> findAllByRentalHistoryId(Long rentalHistoryId) {
        return rentalStatusWorkerLogJpaRepository.findAllByRentalHistoryIdOrderByCreatedAtDesc(rentalHistoryId).stream()
            .map(RentalStatusWorkerLogJpaEntity::toDomain)
            .toList();
    }
}
