package kr.ac.kookmin.stream.welfare.domain.rental.repository;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;

public interface RentalHistoryRepository {

    /**
     * 회원의 대여 이력을 신청 시각 내림차순(같으면 식별자 내림차순)으로 조회한다.
     *
     * @param status 대여 상태. null이면 전체
     */
    List<RentalHistory> findAllByMemberId(Long memberId, RentalStatus status);

    /** 모든 회원의 대여 이력 중 해당 상태인 것. 반납 알림 대상을 고를 때 쓴다. */
    List<RentalHistory> findAllByStatus(RentalStatus status);

    Optional<RentalHistory> findById(Long id);

    /**
     * 모든 회원의 대여 이력을 신청 시각 내림차순(같으면 식별자 내림차순)으로 페이지 번호 단위로 조회한다. 관리자 콘솔용.
     *
     * @param status    대여 상태. null이면 전체
     * @param memberIds 대상 회원 id 목록. null이면 회원 필터 없음(빈 리스트로 호출하지 않는다 — 호출부가 먼저 걸러낸다)
     */
    PageResult<RentalHistory> search(RentalStatus status, List<Long> memberIds, PageOffset pageOffset);

    RentalHistory save(RentalHistory history);

    /** 같은 회원이 같은 물품에 아직 끝나지 않은(진행 중인) 이력이 있는지 확인한다. 승인 대기 중인 중복 신청도 막는다. */
    boolean existsActiveRental(Long itemId, Long memberId);

    /** 이 물품에 아직 끝나지 않은(RETURNED/CANCEL/REJECTED가 아닌) 대여 기록이 있는지 확인한다. 물품 삭제 차단에 쓴다. */
    boolean existsActiveRentalForItem(Long itemId);

    /** 반납 신청 대상 조회. 본인 소유가 아니거나 대여 중(RENTAL·OVERDUE) 상태가 아니면 빈 값이다(둘 다 RENTAL_NOT_FOUND로 처리). */
    Optional<RentalHistory> findRentalToReturn(Long id, Long memberId);

    /** 취소 대상 조회. 본인 소유가 아니거나 승인 대기(PENDING) 상태가 아니면 빈 값이다(둘 다 RENTAL_NOT_FOUND로 처리). */
    Optional<RentalHistory> findPendingToCancel(Long id, Long memberId);
}
