package kr.ac.kookmin.stream.welfare.domain.rental.service;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;

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
     * 반납 신청. 본인 소유의 대여 중(RENTAL) 이력만 대상이며, 반납 대기(RETURN_PENDING)로 전이한다.
     * 실제 반납 확인(재고 복구)은 운영진이 {@link #changeStatusByAdmin}으로 최종 확인해야 일어난다.
     */
    RentalHistory requestReturn(Long memberId, Long historyId);

    /**
     * 본인이 걸어 둔 대여 신청을 취소한다. 아직 운영진이 손대지 않은 승인 대기(PENDING) 이력만 대상이다.
     * 재고 복구에 필요한 물품 ID·수량을 호출부가 쓸 수 있도록 갱신된 이력을 돌려준다.
     */
    RentalHistory cancel(Long memberId, Long historyId);

    /** 같은 회원이 같은 물품에 아직 끝나지 않은(진행 중인) 이력이 있는지 확인한다. 중복 신청 차단에 쓴다. */
    boolean existsActiveRental(Long itemId, Long memberId);

    /**
     * 새 이력을 만들고 저장한다. workerId는 학생 본인 신청이면 null, 관리자가 현장에서 대신 등록한 것이면
     * 호출한 관리자가 아니라 폼에서 고른 근무자다.
     */
    RentalHistory create(Item item, Long memberId, Long workerId, int count, LocalDateTime appliedAt, LocalDateTime rentAt);
}
