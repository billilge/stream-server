package kr.ac.kookmin.stream.db.welfare;

import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;
import kr.ac.kookmin.stream.welfare.domain.rental.repository.RentalHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RentalHistoryRepositoryImpl implements RentalHistoryRepository {

    private final RentalHistoryJpaRepository rentalHistoryJpaRepository;

    @Override
    public List<RentalHistory> findAllByMemberId(Long memberId, RentalStatus status) {
        return rentalHistoryJpaRepository.findAllByMemberId(memberId, status).stream()
            .map(RentalHistoryJpaEntity::toDomain)
            .toList();
    }
}
