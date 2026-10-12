package kr.ac.kookmin.stream.db.welfare;

import java.util.List;
import java.util.Optional;
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

    @Override
    public RentalHistory save(RentalHistory history) {
        return rentalHistoryJpaRepository.save(RentalHistoryJpaEntity.from(history)).toDomain();
    }

    @Override
    public boolean existsActiveRental(Long itemId, Long memberId) {
        return rentalHistoryJpaRepository.existsByItemIdAndMemberIdAndRentalStatus(itemId, memberId, RentalStatus.RENTAL);
    }

    @Override
    public Optional<RentalHistory> findRentalToReturn(Long id, Long memberId) {
        return rentalHistoryJpaRepository.findByIdAndMemberIdAndRentalStatus(id, memberId, RentalStatus.RENTAL)
            .map(RentalHistoryJpaEntity::toDomain);
    }
}
