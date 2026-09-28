package kr.ac.kookmin.stream.welfare.domain.rental.service.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistorySummary;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ReturnRequiredRental;
import kr.ac.kookmin.stream.welfare.domain.rental.repository.ItemRepository;
import kr.ac.kookmin.stream.welfare.domain.rental.repository.RentalHistoryRepository;
import kr.ac.kookmin.stream.welfare.domain.rental.service.RentalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class RentalHistoryServiceImpl implements RentalHistoryService {

    private final RentalHistoryRepository rentalHistoryRepository;
    private final ItemRepository itemRepository;

    // 이력과 물품을 두 번에 나눠 읽어 서비스가 짝짓는다(coding-style.md 2-6). 지금은 items에 쓰기 경로가
    // 없어 두 조회 사이에 물품이 바뀔 수 없으므로 트랜잭션이 필요 없다. 물품 이름 수정·신규 등록 정도만
    // 생기는 한 이 필드는 필터·계산에 안 쓰여 트랜잭션 없이도 안전하다. 반납 정책·타입처럼 hasDueAt·dueAt
    // 계산에 쓰이는 필드를 수정하는 기능이 생기면 그때 @Transactional(readOnly = true)를 다시 붙인다.
    @Override
    public List<RentalHistorySummary> getHistories(Long memberId, RentalStatus status) {
        List<RentalHistory> histories = rentalHistoryRepository.findAllByMemberId(memberId, status);
        Map<Long, Item> items = findItemsOf(histories);

        return histories.stream()
            .map(history -> RentalHistorySummary.of(history, items.get(history.getItemId())))
            .toList();
    }

    // getHistories와 같은 이유로 트랜잭션이 필요 없다. hasDueAt이 보는 returnPolicy는 지금 계획된
    // 쓰기(이름 수정·신규 등록)로는 바뀌지 않으므로, 두 조회 사이에 반납 필요 여부가 달라지지 않는다.
    @Override
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
}
