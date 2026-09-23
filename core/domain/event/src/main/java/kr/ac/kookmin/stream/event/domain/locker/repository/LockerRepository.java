package kr.ac.kookmin.stream.event.domain.locker.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSection;

public interface LockerRepository {

    /**
     * 게시된 운영 회차가 있는지. 아직 공개하지 않은 회차는 학생에게 없는 것으로 보여야 한다.
     * <p>
     * 조회 API는 회차의 존재만 확인하면 되므로 회차 자체는 읽지 않는다. 신청 기간 판정처럼 회차 값이
     * 필요해지면 그때 조회 메서드를 추가한다.
     */
    boolean existsPublishedPeriod(Long lockerPeriodId);

    boolean existsSection(Long sectionId);

    /**
     * 전체 구역을 식별자 오름차순으로 조회한다. 화면이 구역 자리를 알고 채우므로 별도 표시 순서는 두지 않고,
     * 같은 요청이 항상 같은 순서를 돌려주도록 식별자로만 정렬한다.
     */
    List<LockerSection> findAllSections();

    /**
     * 삭제되지 않은 사물함 전체를 조회한다. 구역별 집계는 서비스가 이 목록을 묶어 센다.
     * <p>
     * 선택 가능 판정({@code Locker.isSelectable})을 SQL로 옮기지 않으려는 선택이다. 사물함은 물리적 수량이라
     * 규모가 유한하고 구역 목록은 어차피 전부 내려가므로 전부 읽어도 부담이 없다.
     */
    List<Locker> findAllLockers();

    /**
     * 구역에 속한 사물함을 배치도 순서(행 → 열)로 조회한다.
     */
    List<Locker> findLockersBySectionId(Long sectionId);

    /**
     * 해당 운영 회차에 이미 신청된 사물함 식별자.
     */
    Set<Long> findAppliedLockerIds(Long lockerPeriodId);

    /**
     * 해당 운영 회차에서 회원이 신청한 사물함 식별자. 회차당 한 건만 신청할 수 있고 취소가 없어 최대 하나다.
     * <p>
     * 어느 구역인지는 이미 읽어둔 사물함 목록에서 가려낼 수 있어 식별자만 돌려준다.
     */
    Optional<Long> findAppliedLockerId(Long lockerPeriodId, Long memberId);
}
