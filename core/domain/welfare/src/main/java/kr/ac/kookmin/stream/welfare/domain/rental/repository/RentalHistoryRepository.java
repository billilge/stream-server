package kr.ac.kookmin.stream.welfare.domain.rental.repository;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;

public interface RentalHistoryRepository {

    /**
     * 회원의 대여 이력을 신청 시각 내림차순(같으면 식별자 내림차순)으로 조회한다.
     *
     * @param status 대여 상태. null이면 전체
     */
    List<RentalHistory> findAllByMemberId(Long memberId, RentalStatus status);

    RentalHistory save(RentalHistory history);

    /** 같은 회원이 같은 물품을 이미 대여 중인지(상태가 RENTAL인 이력이 있는지) 확인한다. */
    boolean existsActiveRental(Long itemId, Long memberId);

    /** 반납 신청 대상 조회. 본인 소유가 아니거나 대여 중 상태가 아니면 빈 값이다(둘 다 RENTAL_NOT_FOUND로 처리). */
    Optional<RentalHistory> findRentalToReturn(Long id, Long memberId);
}
