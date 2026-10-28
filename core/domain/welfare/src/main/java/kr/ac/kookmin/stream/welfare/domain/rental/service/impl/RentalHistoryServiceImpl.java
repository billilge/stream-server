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

    // 이력과 물품을 두 번에 나눠 읽어 서비스가 짝짓는다(coding-style.md 2-6). AdminItemService.update()가
    // 물품의 반납 정책·종류를 바꿀 수 있어서(isReturnRequired·dueAt 계산에 직접 쓰이는 필드), 두 조회 사이에
    // 그 값이 바뀌지 않도록 스냅샷을 고정한다.
    @Override
    @Transactional(readOnly = true)
    public List<RentalRecord> getHistories(Long memberId, RentalStatus status) {
        return toRecords(rentalHistoryRepository.findAllByMemberId(memberId, status));
    }

    // getHistories와 같은 이유로 스냅샷을 고정한다.
    @Override
    @Transactional(readOnly = true)
    public List<RentalRecord> getReturnRequiredRentals(Long memberId) {
        return toRecords(rentalHistoryRepository.findAllByMemberId(memberId, null)).stream()
            .filter(RentalRecord::isReturnRequired)
            .toList();
    }

    // getReturnRequiredRentals와 같은 이유로 스냅샷을 고정한다. RentalRecord.isReturnRequired()가 보는
    // 세 상태(RENTAL·RETURN_PENDING·RETURN_CONFIRMED)를 모두 봐야 반납 신청 후 운영진이 최종 확인하기
    // 전까지도 알림·목록에 계속 잡힌다.
    @Override
    @Transactional(readOnly = true)
    public List<RentalRecord> getAllReturnRequiredRentals() {
        List<RentalHistory> candidates = rentalHistoryRepository.findAllByStatusIn(RentalStatus.awaitingReturnStatuses());
        return toRecords(candidates).stream()
            .filter(RentalRecord::isReturnRequired)
            .toList();
    }

    // 조회(대상 확인) + 쓰기(상태 전이)만 있는 단순 전이라 트랜잭션 하나로 끝난다. RETURN_PENDING은 재고를
    // 건드리지 않는다 — 실제 반납 확인(재고 복구)은 운영진이 changeStatusByAdmin으로 RETURNED까지 바꿔야 일어난다.
    @Override
    @Transactional
    public RentalHistory requestReturn(Long memberId, Long historyId) {
        RentalHistory history = getOwnedByStatus(historyId, memberId, RentalStatus.RENTAL);
        history.requestReturn();
        return rentalHistoryRepository.save(history);
    }

    // PENDING 상태에서만 가능하므로 재고 복구가 항상 일어난다(운영진이 아직 손대지 않은 신청이라 중복 복구
    // 우려가 없다). 재고(Item)는 다른 집합체라 완료된 이력을 돌려주고 호출부가 ItemService와 조합한다.
    @Override
    @Transactional
    public RentalHistory cancel(Long memberId, Long historyId) {
        RentalHistory history = getOwnedByStatus(historyId, memberId, RentalStatus.PENDING);
        history.cancel();
        return rentalHistoryRepository.save(history);
    }

    // requestReturn·cancel이 공유하는 "본인 소유 + 특정 상태" 조회. 둘 다 소유자가 아니거나 상태가 다르면
    // 구분 없이 RENTAL_NOT_FOUND로 처리한다(존재는 하지만 남의 것이거나 상태가 안 맞는다는 걸 굳이 알리지 않는다).
    private RentalHistory getOwnedByStatus(Long historyId, Long memberId, RentalStatus status) {
        return rentalHistoryRepository.findById(historyId)
            .filter(history -> history.getMemberId().equals(memberId) && history.getRentalStatus() == status)
            .orElseThrow(() -> new BusinessException(RentalErrorCode.RENTAL_NOT_FOUND));
    }

    // 단일 조회라 dirty-checking 이득이 없어 트랜잭션을 걸지 않는다. 대여 신청(RentalApplyUseCase)에서는
    // LockExecutor가 시도마다 연 트랜잭션 안에서 호출되므로, 그 시도의 스냅샷을 그대로 공유해서 쓴다.
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
