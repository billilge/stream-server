package kr.ac.kookmin.stream.event.domain.locker.service;

import java.util.List;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerAvailability;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionSummary;

public interface LockerService {

    /**
     * 운영 회차의 구역별 전체/선택 가능 사물함 수와 표시 상태를 조회한다.
     */
    List<LockerSectionSummary> getSections(Long lockerPeriodId, Long memberId);

    /**
     * 구역에 속한 사물함의 배치 정보와 선택 가능 여부를 조회한다.
     */
    List<LockerAvailability> getSectionLockers(Long lockerPeriodId, Long sectionId, Long memberId);
}
