package kr.ac.kookmin.stream.db.welfare;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
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

    // 완전히 끝난(더는 손댈 일 없는) 상태. 물품 삭제 차단은 이 셋이 아닌 모든 기록을 "진행 중"으로 본다.
    private static final Set<RentalStatus> TERMINAL_STATUSES = Set.of(RentalStatus.RETURNED, RentalStatus.CANCEL, RentalStatus.REJECTED);

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
        return rentalHistoryJpaRepository.existsByItemIdAndRentalStatusNotIn(itemId, TERMINAL_STATUSES);
    }
}
