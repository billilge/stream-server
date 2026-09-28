package kr.ac.kookmin.stream.welfare.domain.rental.service;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistorySummary;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ReturnRequiredRental;

public interface RentalHistoryService {

    /**
     * 회원의 대여 이력을 최근 신청순으로 조회한다.
     *
     * @param status 대여 상태. null이면 전체
     */
    List<RentalHistorySummary> getHistories(Long memberId, RentalStatus status);

    /**
     * 회원이 지금 반납해야 하는 대여(대여 중 상태)와 반납 기한을 조회한다.
     * 반납 정책이 없는 물품의 대여는 기한을 계산할 수 없어 제외한다.
     */
    List<ReturnRequiredRental> getReturnRequiredRentals(Long memberId);

    /**
     * 반납 신청. 본인 소유의 대여 중(RENTAL) 이력만 대상이며, 신청 즉시 반납 완료로 전이한다
     * (운영진의 최종 확인 단계가 없어서다).
     */
    void returnRental(Long memberId, Long historyId);

    /** 같은 회원이 같은 물품을 이미 대여 중인지(상태가 RENTAL인 이력이 있는지) 확인한다. */
    boolean existsActiveRental(Long itemId, Long memberId);

    /** 대여 신청으로 새 이력을 만들고 저장한다. */
    RentalHistory create(Item item, Long memberId, int count, LocalDateTime appliedAt, LocalDateTime rentAt);
}
