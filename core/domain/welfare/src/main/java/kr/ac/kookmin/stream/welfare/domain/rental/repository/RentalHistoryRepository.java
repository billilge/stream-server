package kr.ac.kookmin.stream.welfare.domain.rental.repository;

import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;

public interface RentalHistoryRepository {

    /**
     * 회원의 대여 이력을 신청 시각 내림차순(같으면 식별자 내림차순)으로 조회한다.
     *
     * @param status 대여 상태. null이면 전체
     */
    List<RentalHistory> findAllByMemberId(Long memberId, RentalStatus status);
}
