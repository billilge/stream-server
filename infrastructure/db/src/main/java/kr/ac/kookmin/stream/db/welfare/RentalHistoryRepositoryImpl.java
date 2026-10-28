package kr.ac.kookmin.stream.db.welfare;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalErrorCode;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;
import kr.ac.kookmin.stream.welfare.domain.rental.repository.RentalHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public List<RentalHistory> findAllByStatus(RentalStatus status) {
        return rentalHistoryJpaRepository.findAllByRentalStatus(status).stream()
            .map(RentalHistoryJpaEntity::toDomain)
            .toList();
    }

    @Override
    public Optional<RentalHistory> findById(Long id) {
        return rentalHistoryJpaRepository.findById(id).map(RentalHistoryJpaEntity::toDomain);
    }

    @Override
    public RentalHistory save(RentalHistory history) {
        return rentalHistoryJpaRepository.save(RentalHistoryJpaEntity.from(history)).toDomain();
    }

    @Override
    public boolean existsActiveRental(Long itemId, Long memberId) {
        return rentalHistoryJpaRepository.existsByItemIdAndMemberIdAndRentalStatusNotIn(
            itemId, memberId, RentalStatus.finishedStatuses()
        );
    }

    @Override
    public Optional<RentalHistory> findRentalToReturn(Long id, Long memberId) {
        return rentalHistoryJpaRepository.findByIdAndMemberIdAndRentalStatus(id, memberId, RentalStatus.RENTAL)
            .map(RentalHistoryJpaEntity::toDomain);
    }

    @Override
    public RentalHistory getRentalToReturn(Long id, Long memberId) {
        return findRentalToReturn(id, memberId)
            .orElseThrow(() -> new BusinessException(RentalErrorCode.RENTAL_NOT_FOUND));
    }

    @Override
    public Optional<RentalHistory> findPendingToCancel(Long id, Long memberId) {
        return rentalHistoryJpaRepository.findByIdAndMemberIdAndRentalStatus(id, memberId, RentalStatus.PENDING)
            .map(RentalHistoryJpaEntity::toDomain);
    }

    @Override
    public RentalHistory getPendingToCancel(Long id, Long memberId) {
        return findPendingToCancel(id, memberId)
            .orElseThrow(() -> new BusinessException(RentalErrorCode.RENTAL_NOT_FOUND));
    }

    @Override
    public PageResult<RentalHistory> search(RentalStatus status, List<Long> memberIds, PageOffset pageOffset) {
        boolean filterByMember = memberIds != null;
        List<Long> ids = filterByMember ? memberIds : List.of();

        Page<RentalHistoryJpaEntity> result = rentalHistoryJpaRepository.search(
            status, filterByMember, ids, PageRequest.of(pageOffset.page(), pageOffset.size())
        );

        return PageResult.of(
            result.getContent().stream().map(RentalHistoryJpaEntity::toDomain).toList(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements()
        );
    }

    @Override
    public boolean existsActiveRentalForItem(Long itemId) {
        return rentalHistoryJpaRepository.existsByItemIdAndRentalStatusNotIn(itemId, RentalStatus.finishedStatuses());
    }
}
