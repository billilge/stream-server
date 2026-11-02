package kr.ac.kookmin.stream.welfare.domain.rental.repository;

import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatusWorkerLog;

public interface RentalStatusWorkerLogRepository {

    RentalStatusWorkerLog save(RentalStatusWorkerLog log);

    /** 대여 이력 하나의 처리자 로그를 변경 시각 내림차순으로 조회한다. */
    List<RentalStatusWorkerLog> findAllByRentalHistoryId(Long rentalHistoryId);
}
