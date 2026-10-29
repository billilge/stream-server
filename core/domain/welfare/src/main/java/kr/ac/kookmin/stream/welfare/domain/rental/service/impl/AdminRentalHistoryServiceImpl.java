package kr.ac.kookmin.stream.welfare.domain.rental.service.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;
import kr.ac.kookmin.stream.welfare.domain.rental.repository.ItemRepository;
import kr.ac.kookmin.stream.welfare.domain.rental.repository.RentalHistoryRepository;
import kr.ac.kookmin.stream.welfare.domain.rental.service.AdminRentalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class AdminRentalHistoryServiceImpl implements AdminRentalHistoryService {

    private final RentalHistoryRepository rentalHistoryRepository;
    private final ItemRepository itemRepository;

    // 단일 조회라 트랜잭션이 필요 없다. 페이지 내용만큼만 물품을 모아 짝짓는다(coding-style.md 2-6).
    @Override
    public PageResult<RentalRecord> search(RentalStatus status, List<Long> memberIds, PageOffset pageOffset) {
        PageResult<RentalHistory> result = rentalHistoryRepository.search(status, memberIds, pageOffset);
        Map<Long, Item> itemsByItemId = findItemsByItemId(result.content());
        return result.map(history -> new RentalRecord(history, itemsByItemId.get(history.getItemId())));
    }

    private Map<Long, Item> findItemsByItemId(List<RentalHistory> histories) {
        if (histories.isEmpty()) {
            return Map.of();
        }
        List<Long> itemIds = histories.stream().map(RentalHistory::getItemId).distinct().toList();
        return itemRepository.findAllByIds(itemIds).stream()
            .collect(Collectors.toMap(Item::getId, Function.identity()));
    }
}
