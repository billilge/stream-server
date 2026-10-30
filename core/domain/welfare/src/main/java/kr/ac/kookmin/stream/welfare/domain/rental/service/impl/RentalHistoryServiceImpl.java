package kr.ac.kookmin.stream.welfare.domain.rental.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalErrorCode;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;
import kr.ac.kookmin.stream.welfare.domain.rental.repository.ItemRepository;
import kr.ac.kookmin.stream.welfare.domain.rental.repository.RentalHistoryRepository;
import kr.ac.kookmin.stream.welfare.domain.rental.service.RentalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class RentalHistoryServiceImpl implements RentalHistoryService {

    private final RentalHistoryRepository rentalHistoryRepository;
    private final ItemRepository itemRepository;

    // 회원의 대여 이력을 물품과 짝지어 반환한다.
    @Override
    public List<RentalRecord> getHistories(Long memberId, RentalStatus status) {
        return toRecords(rentalHistoryRepository.findAllByMemberId(memberId, status));
    }

    @Override
    public List<RentalRecord> getReturnRequiredRentals(Long memberId) {
        return toRecords(rentalHistoryRepository.findAllByMemberId(memberId, null)).stream()
            .filter(RentalRecord::isReturnRequired)
            .toList();
    }

    // 반납 기한 임박·연체 알림 대상을 고를 때 쓴다.
    @Override
    public List<RentalRecord> getAllReturnRequiredRentals() {
        List<RentalHistory> candidates = rentalHistoryRepository.findAllByStatusIn(RentalStatus.awaitingReturnStatuses());
        return toRecords(candidates).stream()
            .filter(RentalRecord::isReturnRequired)
            .toList();
    }

    @Override
    @Transactional
    public RentalHistory requestReturn(Long memberId, Long historyId) {
        RentalHistory history = getOwnedByStatus(historyId, memberId, RentalStatus.RENTAL);
        history.requestReturn();
        return rentalHistoryRepository.save(history);
    }

    @Override
    @Transactional
    public RentalHistory cancel(Long memberId, Long historyId) {
        RentalHistory history = getOwnedByStatus(historyId, memberId, RentalStatus.PENDING);
        history.cancel();
        return rentalHistoryRepository.save(history);
    }

    // requestReturn·cancel이 공유하는 "본인 소유 + 특정 상태" 조회. 조건에 안 맞으면 RENTAL_NOT_FOUND.
    private RentalHistory getOwnedByStatus(Long historyId, Long memberId, RentalStatus status) {
        return rentalHistoryRepository.findById(historyId)
            .filter(history -> history.getMemberId().equals(memberId) && history.getRentalStatus() == status)
            .orElseThrow(() -> new BusinessException(RentalErrorCode.RENTAL_NOT_FOUND));
    }

    @Override
    public boolean existsActiveRental(Long itemId, Long memberId) {
        return rentalHistoryRepository.existsActiveRental(itemId, memberId);
    }

    @Override
    @Transactional
    public RentalHistory create(Item item, Long memberId, Long workerId, int count, LocalDateTime appliedAt, LocalDateTime rentAt) {
        return rentalHistoryRepository.save(RentalHistory.create(item, memberId, workerId, count, appliedAt, rentAt));
    }

    private List<RentalRecord> toRecords(List<RentalHistory> histories) {
        Map<Long, Item> itemsByItemId = findItemMapByHistories(histories);
        return histories.stream()
            .map(history -> new RentalRecord(history, itemsByItemId.get(history.getItemId())))
            .toList();
    }

    private Map<Long, Item> findItemMapByHistories(List<RentalHistory> histories) {
        if (histories.isEmpty()) {
            return Map.of();
        }
        List<Long> itemIds = histories.stream().map(RentalHistory::getItemId).distinct().toList();
        return itemRepository.findAllByIds(itemIds).stream()
            .collect(Collectors.toMap(Item::getId, Function.identity()));
    }
}
