package kr.ac.kookmin.stream.welfare.domain.rental.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.DateUtil;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
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

    // 이력과 물품을 두 번에 나눠 읽어 서비스가 짝짓는다(coding-style.md 2-6). 지금은 items에 쓰기 경로가
    // 없어 두 조회 사이에 물품이 바뀔 수 없으므로 트랜잭션이 필요 없다. 물품 이름 수정·신규 등록 정도만
    // 생기는 한 이 필드는 필터·계산에 안 쓰여 트랜잭션 없이도 안전하다. 반납 정책·타입처럼 isReturnRequired·dueAt
    // 계산에 쓰이는 필드를 수정하는 기능이 생기면 그때 @Transactional(readOnly = true)를 다시 붙인다.
    @Override
    public List<RentalRecord> getHistories(Long memberId, RentalStatus status) {
        return toRecords(rentalHistoryRepository.findAllByMemberId(memberId, status));
    }

    // getHistories와 같은 이유로 트랜잭션이 필요 없다. isReturnRequired가 보는 returnPolicy는 지금 계획된
    // 쓰기(이름 수정·신규 등록)로는 바뀌지 않으므로, 두 조회 사이에 반납 필요 여부가 달라지지 않는다.
    @Override
    public List<RentalRecord> getReturnRequiredRentals(Long memberId) {
        return toRecords(rentalHistoryRepository.findAllByMemberId(memberId, null)).stream()
            .filter(RentalRecord::isReturnRequired)
            .toList();
    }

    // getReturnRequiredRentals와 같은 이유로 트랜잭션이 필요 없다. 연체 여부는 상태가 아니라 dueAt으로
    // 조회 시점에 판단하므로(RentalRecord.isReturnRequired), 상태는 RENTAL 하나만 보면 된다.
    @Override
    public List<RentalRecord> getAllReturnRequiredRentals() {
        return toRecords(rentalHistoryRepository.findAllByStatus(RentalStatus.RENTAL)).stream()
            .filter(RentalRecord::isReturnRequired)
            .toList();
    }

    // 조회(대상 확인) + 쓰기(상태 전이)가 원자적으로 묶여야 하므로 일반 트랜잭션이다. 재고 복구는
    // RentalHistory만으로 할 수 없는 다른 집합체(Item)의 책임이라, 완료된 이력을 돌려주고 호출부
    // (api:app-api의 UseCase)가 ItemService와 조합한다(coding-style.md 2-9절).
    @Override
    @Transactional
    public RentalHistory returnRental(Long memberId, Long historyId) {
        RentalHistory history = rentalHistoryRepository.getRentalToReturn(historyId, memberId);
        history.markReturned(LocalDateTime.now(DateUtil.KST));
        return rentalHistoryRepository.save(history);
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

    // 단일 조회라 트랜잭션이 필요 없다. 페이지 내용만큼만 물품을 모아 짝짓는다(toRecords와 같은 이유).
    @Override
    public PageResult<RentalRecord> search(RentalStatus status, List<Long> memberIds, PageOffset pageOffset) {
        PageResult<RentalHistory> result = rentalHistoryRepository.search(status, memberIds, pageOffset);
        Map<Long, Item> itemsByItemId = findItemsByItemId(result.content());
        return result.map(history -> new RentalRecord(history, itemsByItemId.get(history.getItemId())));
    }

    private List<RentalRecord> toRecords(List<RentalHistory> histories) {
        Map<Long, Item> itemsByItemId = findItemsByItemId(histories);
        return histories.stream()
            .map(history -> new RentalRecord(history, itemsByItemId.get(history.getItemId())))
            .toList();
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
