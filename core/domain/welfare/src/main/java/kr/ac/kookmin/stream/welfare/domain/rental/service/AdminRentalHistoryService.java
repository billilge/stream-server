package kr.ac.kookmin.stream.welfare.domain.rental.service;

import java.util.List;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatusWorkerLog;

/** 운영진 전용 대여 이력 조회·상태 변경. 학생 앱이 쓰는 {@link RentalHistoryService}와 분리한다(coding-style.md 2-7절). */
public interface AdminRentalHistoryService {

    /**
     * 모든 회원의 대여 이력을 신청 시각 내림차순으로 페이지 번호 단위로 조회한다.
     *
     * @param status    대여 상태. null이면 전체
     * @param memberIds 대상 회원 id 목록. null이면 회원 필터 없음
     */
    PageResult<RentalRecord> search(RentalStatus status, List<Long> memberIds, PageOffset pageOffset);

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
