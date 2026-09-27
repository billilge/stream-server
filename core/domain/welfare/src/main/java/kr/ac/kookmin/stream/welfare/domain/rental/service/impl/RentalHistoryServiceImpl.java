package kr.ac.kookmin.stream.welfare.domain.rental.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalErrorCode;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistorySummary;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ReturnRequiredRental;
import kr.ac.kookmin.stream.welfare.domain.rental.repository.ItemRepository;
import kr.ac.kookmin.stream.welfare.domain.rental.repository.RentalHistoryRepository;
import kr.ac.kookmin.stream.welfare.domain.rental.service.RentalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class RentalHistoryServiceImpl implements RentalHistoryService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final RentalHistoryRepository rentalHistoryRepository;
    private final ItemRepository itemRepository;

    // 이력과 물품을 두 번에 나눠 읽어 서비스가 짝짓는다. 두 조회가 한 트랜잭션(같은 스냅샷)에 묶여야
    // 그 사이에 바뀐 물품 때문에 이력과 물품 정보가 어긋나지 않는다. 쓰기가 없으니 readOnly다.
    @Override
    @Transactional(readOnly = true)
    public List<RentalHistorySummary> getHistories(Long memberId, RentalStatus status) {
        List<RentalHistory> histories = rentalHistoryRepository.findAllByMemberId(memberId, status);
        Map<Long, Item> items = findItemsOf(histories);

        return histories.stream()
            .map(history -> RentalHistorySummary.of(history, items.get(history.getItemId())))
            .toList();
    }

    // getHistories와 같은 이유(이력 + 물품 두 조회의 스냅샷 일관성, 쓰기 없음)로 readOnly다.
    @Override
    @Transactional(readOnly = true)
    public List<ReturnRequiredRental> getReturnRequiredRentals(Long memberId) {
        List<RentalHistory> histories = rentalHistoryRepository.findAllByMemberId(memberId, RentalStatus.RENTAL);
        Map<Long, Item> items = findItemsOf(histories);

        return histories.stream()
            .filter(history -> hasDueAt(history, items.get(history.getItemId())))
            .map(history -> ReturnRequiredRental.of(history, items.get(history.getItemId())))
            .toList();
    }

    private Map<Long, Item> findItemsOf(List<RentalHistory> histories) {
        if (histories.isEmpty()) {
            return Map.of();
        }
        List<Long> itemIds = histories.stream().map(RentalHistory::getItemId).distinct().toList();
        return itemRepository.findAllByIds(itemIds).stream()
            .collect(Collectors.toMap(Item::getId, Function.identity()));
    }

    // 대여 시각과 물품의 반납 정책이 모두 있어야 반납 기한을 계산할 수 있다
    private boolean hasDueAt(RentalHistory history, Item item) {
        return history.getRentAt() != null && item != null && item.getReturnPolicy() != null;
    }

    // 조회(대상 확인) + 쓰기(상태 전이)가 원자적으로 묶여야 하므로 일반 트랜잭션이다.
    @Override
    @Transactional
    public void returnRental(Long memberId, Long historyId) {
        RentalHistory history = rentalHistoryRepository.findRentalToReturn(historyId, memberId)
            .orElseThrow(() -> new BusinessException(RentalErrorCode.RENTAL_NOT_FOUND));
        history.markReturned(LocalDateTime.now(KST));
        rentalHistoryRepository.save(history);
    }
}
