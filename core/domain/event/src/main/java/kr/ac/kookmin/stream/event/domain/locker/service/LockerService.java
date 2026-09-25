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
     * 조회하는 쪽이 자기 사물함을 보는지 남의 것을 보는지 가리지 않는다. 학생 앱은 요청한 회원의
     * 식별자를 넘겨 "내 사물함"을 표시하고, 운영진 화면처럼 다른 회원의 배정을 확인해야 하는 곳도
     * 같은 메서드를 쓴다. 노출 범위 판단은 호출하는 쪽의 몫이다.
     * <p>
     * 회차 게시 여부는 확인하지 않는다. 신청은 회차 공개 여부와 무관하게 이미 이뤄진 사실이고, 학생 앱
     * 조회 엔드포인트는 구역 조회에서 미게시 회차를 먼저 404로 거른 뒤 이 메서드를 쓴다.
     */
    Optional<Locker> getLockerByMemberId(Long lockerPeriodId, Long memberId);
}
