package kr.ac.kookmin.stream.welfare.domain.rental.service;

import java.util.List;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;

/** 운영진 전용 대여 이력 조회·상태 변경. 학생 앱이 쓰는 {@link RentalHistoryService}와 분리한다(coding-style.md 2-7절). */
public interface AdminRentalHistoryService {

    /**
     * 모든 회원의 대여 이력을 신청 시각 내림차순으로 페이지 번호 단위로 조회한다.
     *
     * @param status    대여 상태. null이면 전체
     * @param memberIds 대상 회원 id 목록. null이면 회원 필터 없음
     */
    PageResult<RentalRecord> search(RentalStatus status, List<Long> memberIds, PageOffset pageOffset);
}
