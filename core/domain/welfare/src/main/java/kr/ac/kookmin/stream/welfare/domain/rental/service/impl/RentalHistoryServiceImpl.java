package kr.ac.kookmin.stream.welfare.domain.rental.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.DateUtil;
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
import kr.ac.kookmin.stream.welfare.domain.rental.service.RentalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class RentalHistoryServiceImpl implements RentalHistoryService {

    private final RentalHistoryRepository rentalHistoryRepository;
    private final ItemRepository itemRepository;
    private final RentalStatusWorkerLogRepository rentalStatusWorkerLogRepository;

    // 이력과 물품을 두 번에 나눠 읽어 서비스가 짝짓는다(coding-style.md 2-6). AdminItemService.update()가
    // 물품의 반납 정책·종류를 바꿀 수 있어서(isReturnRequired·dueAt 계산에 직접 쓰이는 필드), 두 조회 사이에
    // 그 값이 바뀌지 않도록 스냅샷을 고정한다.
    @Override
    @Transactional(readOnly = true)
    public List<RentalRecord> getHistories(Long memberId, RentalStatus status) {
        return toRecords(rentalHistoryRepository.findAllByMemberId(memberId, status));
    }

    // getHistories와 같은 이유로 스냅샷을 고정한다. status를 null로 둬 전체를 가져온 뒤 isReturnRequired()로
    // 거른다 — RENTAL뿐 아니라 OVERDUE도 걸러야 해서, 한 회원의 이력은 수가 적어 이렇게 걸러도 비용이 크지 않다.
    @Override
    @Transactional(readOnly = true)
    public List<RentalRecord> getReturnRequiredRentals(Long memberId) {
        return toRecords(rentalHistoryRepository.findAllByMemberId(memberId, null)).stream()
            .filter(RentalRecord::isReturnRequired)
            .toList();
    }

    // getReturnRequiredRentals와 같은 이유로 스냅샷을 고정한다. RentalRecord.isReturnRequired()가 보는
    // 네 상태(RENTAL·OVERDUE·RETURN_PENDING·RETURN_CONFIRMED)를 모두 봐야 반납 신청 후 운영진이 최종
    // 확인하기 전까지도 알림·목록에 계속 잡힌다. 레포지토리에 "여러 상태" 전용 메서드를 새로 만드는 대신,
    // 이미 있는 단일 상태 조회를 네 번 불러 합친다(관리자 "대여/반납 현황 조회"도 상태 하나로 필터하므로
    // 그 단일 상태 메서드를 그대로 쓸 수 있다 — #172에서 이미 결정).
    @Override
    @Transactional(readOnly = true)
    public List<RentalRecord> getAllReturnRequiredRentals() {
        List<RentalHistory> candidates = Stream.of(
                RentalStatus.RENTAL, RentalStatus.OVERDUE, RentalStatus.RETURN_PENDING, RentalStatus.RETURN_CONFIRMED
            )
            .flatMap(status -> rentalHistoryRepository.findAllByStatus(status).stream())
            .toList();
        return toRecords(candidates).stream()
            .filter(RentalRecord::isReturnRequired)
            .toList();
    }

    // 조회(대상 확인) + 쓰기(상태 전이)만 있는 단순 전이라 트랜잭션 하나로 끝난다. RETURN_PENDING은 재고를
    // 건드리지 않는다 — 실제 반납 확인(재고 복구)은 운영진이 changeStatusByAdmin으로 RETURNED까지 바꿔야 일어난다.
    @Override
    @Transactional
    public RentalHistory requestReturn(Long memberId, Long historyId) {
        RentalHistory history = rentalHistoryRepository.findRentalToReturn(historyId, memberId)
            .orElseThrow(() -> new BusinessException(RentalErrorCode.RENTAL_NOT_FOUND));
        history.requestReturn();
        return rentalHistoryRepository.save(history);
    }

    // PENDING 상태에서만 가능하므로 재고 복구가 항상 일어난다(운영진이 아직 손대지 않은 신청이라 중복 복구
    // 우려가 없다). 재고(Item)는 다른 집합체라 완료된 이력을 돌려주고 호출부가 ItemService와 조합한다.
    @Override
    @Transactional
    public RentalHistory cancelByMember(Long memberId, Long historyId) {
        RentalHistory history = rentalHistoryRepository.findPendingToCancel(historyId, memberId)
            .orElseThrow(() -> new BusinessException(RentalErrorCode.RENTAL_NOT_FOUND));
        history.cancelByMember();
        return rentalHistoryRepository.save(history);
    }

    @Override
    @Transactional
    public void markOverdue(RentalHistory history) {
        history.markOverdue();
        rentalHistoryRepository.save(history);
    }

    // 단일 조회라 dirty-checking 이득이 없어 트랜잭션을 걸지 않는다. 대여 신청(RentalApplyUseCase)에서는
    // LockExecutor가 시도마다 연 트랜잭션 안에서 호출되므로, 그 시도의 스냅샷을 그대로 공유해서 쓴다.
    @Override
    public boolean existsActiveRental(Long itemId, Long memberId) {
        return rentalHistoryRepository.existsActiveRental(itemId, memberId);
    }

    @Override
    @Transactional
    public RentalHistory create(Item item, Long memberId, int count, LocalDateTime appliedAt, LocalDateTime rentAt) {
        return rentalHistoryRepository.save(RentalHistory.create(item, memberId, count, appliedAt, rentAt));
    }

    // getHistories와 같은 이유로 스냅샷을 고정한다. 페이지 내용만큼만 물품을 모아 짝짓는다(toRecords와 같은 이유).
    @Override
    @Transactional(readOnly = true)
    public PageResult<RentalRecord> search(RentalStatus status, List<Long> memberIds, PageOffset pageOffset) {
        PageResult<RentalHistory> result = rentalHistoryRepository.search(status, memberIds, pageOffset);
        Map<Long, Item> items = findItemsOf(result.content());
        return result.map(history -> new RentalRecord(history, items.get(history.getItemId())));
    }

    @Override
    @Transactional
    public RentalHistory createByAdmin(Item item, Long memberId, Long workerId, int count, LocalDateTime appliedAt, LocalDateTime rentAt) {
        return rentalHistoryRepository.save(RentalHistory.createByAdmin(item, memberId, workerId, count, appliedAt, rentAt));
    }

    // getHistories와 같은 이유로 스냅샷을 고정한다(호출부가 LockExecutor 안에서 부르면 그 트랜잭션에 합류한다).
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
        history.changeStatusByAdmin(newStatus, LocalDateTime.now(DateUtil.KST));
        RentalHistory saved = rentalHistoryRepository.save(history);
        rentalStatusWorkerLogRepository.save(RentalStatusWorkerLog.create(historyId, workerId, newStatus));
        return saved;
    }

    // 단일 조회라 트랜잭션이 필요 없다. 이력 존재 확인만 먼저 한다.
    @Override
    public List<RentalStatusWorkerLog> getStatusWorkerLogs(Long historyId) {
        if (rentalHistoryRepository.findById(historyId).isEmpty()) {
            throw new BusinessException(RentalErrorCode.RENTAL_NOT_FOUND);
        }
        return rentalStatusWorkerLogRepository.findAllByRentalHistoryId(historyId);
    }

    private List<RentalRecord> toRecords(List<RentalHistory> histories) {
        Map<Long, Item> items = findItemsOf(histories);
        return histories.stream()
            .map(history -> new RentalRecord(history, items.get(history.getItemId())))
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
}
