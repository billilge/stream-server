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

    // 물품을 함께 읽어 서비스가 짝짓는다(coding-style.md 2-6). AdminItemService.update()가 물품의
    // 반납 정책·종류를 바꿀 수 있어서(isReturnRequired·dueAt 계산에 직접 쓰이는 필드), 두 조회 사이에
    // 그 값이 바뀌지 않도록 스냅샷을 고정한다.
    @Override
    @Transactional(readOnly = true)
    public PageResult<RentalRecord> search(RentalStatus status, List<Long> memberIds, PageOffset pageOffset) {
        PageResult<RentalHistory> result = rentalHistoryRepository.search(status, memberIds, pageOffset);
        Map<Long, Item> itemsByItemId = findItemMapByHistories(result.content());
        return result.map(history -> new RentalRecord(history, itemsByItemId.get(history.getItemId())));
    }

    // search와 같은 이유로 스냅샷을 고정한다(호출부가 LockExecutor 안에서 부르면 그 트랜잭션에 합류한다).
    @Override
    @Transactional(readOnly = true)
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
        history.changeStatusByAdmin(newStatus, LocalDateTime.now());
        RentalHistory saved = rentalHistoryRepository.save(history);
        rentalStatusWorkerLogRepository.save(RentalStatusWorkerLog.create(historyId, workerId, newStatus));
        return saved;
    }

    // 단일 조회라 트랜잭션이 필요 없다. 이력 존재 확인만 먼저 한다.
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
