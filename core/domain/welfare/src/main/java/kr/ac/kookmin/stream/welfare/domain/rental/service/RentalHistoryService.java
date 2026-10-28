package kr.ac.kookmin.stream.welfare.domain.rental.service;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatusWorkerLog;

public interface RentalHistoryService {

    /**
     * 회원의 대여 이력을 최근 신청순으로 조회한다.
     *
     * @param status 대여 상태. null이면 전체
     */
    List<RentalRecord> getHistories(Long memberId, RentalStatus status);

    /**
     * 회원이 지금 반납해야 하는 대여를 조회한다. 반납 기한은 {@link RentalRecord#dueAt()}으로 얻는다.
     * 반납 정책이 없는 물품의 대여는 기한을 계산할 수 없어 제외한다.
     */
    List<RentalRecord> getReturnRequiredRentals(Long memberId);

    /**
     * 모든 회원의 반납해야 하는 대여를 조회한다. 반납 기한 임박·연체 알림 대상을 고를 때 쓴다.
     * 반납 정책이 없는 물품의 대여는 기한을 계산할 수 없어 제외한다.
     */
    List<RentalRecord> getAllReturnRequiredRentals();

    /**
     * 반납 신청. 본인 소유의 대여 중(RENTAL·OVERDUE) 이력만 대상이며, 반납 대기(RETURN_PENDING)로 전이한다.
     * 실제 반납 확인(재고 복구)은 운영진이 {@link #changeStatusByAdmin}으로 최종 확인해야 일어난다.
     */
    RentalHistory requestReturn(Long memberId, Long historyId);

    /**
     * 본인이 걸어 둔 대여 신청을 취소한다. 아직 운영진이 손대지 않은 승인 대기(PENDING) 이력만 대상이다.
     * 재고 복구에 필요한 물품 ID·수량을 호출부가 쓸 수 있도록 갱신된 이력을 돌려준다.
     */
    RentalHistory cancelByMember(Long memberId, Long historyId);

    /** 반납 기한이 지난 대여를 연체(OVERDUE)로 표시하고 저장한다. 스케줄러가 매일 호출한다. */
    void markOverdue(RentalHistory history);

    /** 같은 회원이 같은 물품에 아직 끝나지 않은(진행 중인) 이력이 있는지 확인한다. 중복 신청 차단에 쓴다. */
    boolean existsActiveRental(Long itemId, Long memberId);

    /** 대여 신청으로 새 이력을 만들고 저장한다. */
    RentalHistory create(Item item, Long memberId, int count, LocalDateTime appliedAt, LocalDateTime rentAt);

    /**
     * 모든 회원의 대여 이력을 신청 시각 내림차순으로 페이지 번호 단위로 조회한다. 관리자 콘솔용.
     *
     * @param status    대여 상태. null이면 전체
     * @param memberIds 대상 회원 id 목록. null이면 회원 필터 없음
     */
    PageResult<RentalRecord> search(RentalStatus status, List<Long> memberIds, PageOffset pageOffset);

    /**
     * 관리자가 현장 기록을 대신 등록한다. 상태 선택 없이 {@link RentalHistory#createByAdmin}과 같은 기준으로
     * 시작 상태가 정해진다. 처리자(workerId)는 호출한 관리자가 아니라 폼에서 고른 값이다.
     */
    RentalHistory createByAdmin(Item item, Long memberId, Long workerId, int count, LocalDateTime appliedAt, LocalDateTime rentAt);

    /** 대여 이력 하나를 물품과 함께 조회한다. 없으면 {@code RENTAL_NOT_FOUND}. 관리자 상태 변경에 쓴다. */
    RentalRecord getById(Long historyId);

    /**
     * 운영진이 대여 이력 상태를 직접 바꾼다. 전이 규칙은 없다(어떤 값이든 허용). 호출한 관리자(workerId)를
     * 처리자 로그로 남긴다. 재고 복구 여부는 전이 전후 상태로 정해지는데, 재고(Item)는 이 도메인의 다른
     * 집합체라 여기서 판단하지 않는다 — 호출부(api:admin-api의 UseCase)가 {@link #getById}로 미리 읽어 둔
     * 이전 상태와 비교해 판단한다.
     */
    RentalHistory changeStatusByAdmin(Long historyId, RentalStatus newStatus, Long workerId);

    /** 대여 이력 하나의 상태 변경 처리자 로그를 변경 시각 내림차순으로 조회한다. 이력이 없으면 {@code RENTAL_NOT_FOUND}. */
    List<RentalStatusWorkerLog> getStatusWorkerLogs(Long historyId);
}
