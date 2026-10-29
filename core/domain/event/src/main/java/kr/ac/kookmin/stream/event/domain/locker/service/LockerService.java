package kr.ac.kookmin.stream.event.domain.locker.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionDetail;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionSummary;

public interface LockerService {

    /**
     * 운영 회차의 구역별 전체/선택 가능 사물함 수와 표시 상태를 조회한다.
     */
    List<LockerSectionSummary> getSections(Long lockerPeriodId);

    /**
     * 구역 정보와 배치 구조·사진, 구역에 속한 사물함을 함께 조회한다. 배치 구조를 아직 등록하지 않은 구역이면
     * {@link LockerSectionDetail#layout()}이 {@code null}이다.
     * <p>
     * 선택 가능 여부는 담지 않는다. 판정은 {@link Locker#isSelectable(boolean)}이 갖고 있고, 신청 여부는
     * {@link #getAppliedLockerIds(Long)}로 함께 읽어 호출하는 쪽에서 맞춰본다.
     */
    LockerSectionDetail getSectionDetail(Long lockerPeriodId, Long sectionId);

    /**
     * 해당 운영 회차에 이미 신청된 사물함 식별자.
     */
    Set<Long> getAppliedLockerIds(Long lockerPeriodId);

    /**
     * 삭제되지 않은 사물함 전체를 구역별로 묶어 조회한다.
     * <p>
     * 사물함이 한 건도 없는 구역은 키가 없다. 구역 목록을 만들 때처럼 빈 구역도 빠뜨리면 안 되는 쪽은
     * {@code getOrDefault}로 받아야 한다.
     */
    Map<Long, List<Locker>> getLockerMapBySectionId();

    /**
     * 해당 운영 회차에서 회원이 신청한 사물함. 회차당 한 건만 신청할 수 있고 취소가 없어 최대 하나다.
     * <p>
     * 회차 게시 여부와 노출 범위는 확인하지 않는다. 호출하는 쪽이 판단한다.
     */
    Optional<Locker> getLockerByMemberId(Long lockerPeriodId, Long memberId);
}
