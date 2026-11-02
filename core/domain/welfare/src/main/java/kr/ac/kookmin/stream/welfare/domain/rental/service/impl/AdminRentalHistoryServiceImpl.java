package kr.ac.kookmin.stream.welfare.domain.rental.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalErrorCode;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatusWorkerLog;
import kr.ac.kookmin.stream.welfare.domain.rental.repository.ItemRepository;
import kr.ac.kookmin.stream.welfare.domain.rental.repository.RentalHistoryRepository;
import kr.ac.kookmin.stream.welfare.domain.rental.repository.RentalStatusWorkerLogRepository;
import kr.ac.kookmin.stream.welfare.domain.rental.service.AdminRentalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class AdminRentalHistoryServiceImpl implements AdminRentalHistoryService {

    private final RentalHistoryRepository rentalHistoryRepository;
    private final ItemRepository itemRepository;
    private final RentalStatusWorkerLogRepository rentalStatusWorkerLogRepository;

    // 대여 이력을 물품과 짝지어 페이지 단위로 반환한다.
    @Override
    public PageResult<RentalRecord> search(RentalStatus status, List<Long> memberIds, PageOffset pageOffset) {
        PageResult<RentalHistory> result = rentalHistoryRepository.search(status, memberIds, pageOffset);
        Map<Long, Item> itemsByItemId = findItemMapByHistories(result.content());
        return result.map(history -> new RentalRecord(history, itemsByItemId.get(history.getItemId())));
    }

    @Override
    public RentalRecord getById(Long historyId) {
        RentalHistory history = rentalHistoryRepository.findById(historyId)
            .orElseThrow(() -> new BusinessException(RentalErrorCode.RENTAL_NOT_FOUND));
        Item item = itemRepository.findById(history.getItemId()).orElse(null);
        return new RentalRecord(history, item);
    }

    @Override
    @Transactional
    public RentalHistory changeStatusByAdmin(Long historyId, RentalStatus newStatus, Long workerId) {
        RentalHistory history = rentalHistoryRepository.findById(historyId)
            .orElseThrow(() -> new BusinessException(RentalErrorCode.RENTAL_NOT_FOUND));

        // 직전과 같은 상태로의 변경은 아무것도 하지 않는다.
        if (history.getRentalStatus() == newStatus) {
            return history;
        }

        history.changeStatusByAdmin(newStatus, LocalDateTime.now());
        RentalHistory saved = rentalHistoryRepository.save(history);
        rentalStatusWorkerLogRepository.save(RentalStatusWorkerLog.create(historyId, workerId, newStatus));
        return saved;
    }

    @Override
    public List<RentalStatusWorkerLog> getWorkerLogs(Long historyId) {
        if (rentalHistoryRepository.findById(historyId).isEmpty()) {
            throw new BusinessException(RentalErrorCode.RENTAL_NOT_FOUND);
        }
        return rentalStatusWorkerLogRepository.findAllByRentalHistoryId(historyId);
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
