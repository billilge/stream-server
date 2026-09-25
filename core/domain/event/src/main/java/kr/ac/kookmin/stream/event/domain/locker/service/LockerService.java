package kr.ac.kookmin.stream.event.domain.locker.service;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerAvailability;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionSummary;

public interface LockerService {

    /**
     * 운영 회차의 구역별 전체/선택 가능 사물함 수와 표시 상태를 조회한다.
     */
    List<LockerSectionSummary> getSections(Long lockerPeriodId);

    /**
     * 구역에 속한 사물함의 배치 정보와 선택 가능 여부를 조회한다.
     */
    List<LockerAvailability> getSectionLockers(Long lockerPeriodId, Long sectionId);

    /**
     * 해당 운영 회차에서 회원이 신청한 사물함. 회차당 한 건만 신청할 수 있고 취소가 없어 최대 하나다.
     * <p>
     * 회차 게시 여부는 확인하지 않는다. 회원 본인의 신청은 공개 여부와 무관하게 본인 것이고, 조회
     * 엔드포인트는 구역 조회에서 이미 미게시 회차를 걸러낸 뒤 이 메서드를 쓴다.
     */
    Optional<Locker> getMyLocker(Long lockerPeriodId, Long memberId);
}
