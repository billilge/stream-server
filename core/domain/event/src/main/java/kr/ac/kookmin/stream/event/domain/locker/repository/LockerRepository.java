package kr.ac.kookmin.stream.event.domain.locker.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplication;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriod;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSection;

public interface LockerRepository {

    /**
     * 게시된 운영 회차가 있는지. 아직 공개하지 않은 회차는 학생에게 없는 것으로 보여야 한다.
     */
    boolean existsPublishedPeriod(Long lockerPeriodId);

    boolean existsSection(Long sectionId);

    /**
     * 게시된 운영 회차 한 건. 아직 공개하지 않은 회차는 없는 것으로 본다.
     */
    Optional<LockerPeriod> findPublishedPeriodById(Long lockerPeriodId);

    /**
     * 전체 구역을 식별자 오름차순으로 조회한다.
     */
    List<LockerSection> findAllSections();

    /**
     * 삭제되지 않은 사물함 전체를 조회한다. 구역별 집계는 서비스가 이 목록을 묶어 센다.
     */
    List<Locker> findAllLockers();

    /**
     * 구역에 속한 사물함을 배치도 순서(행 → 열)로 조회한다.
     */
    List<Locker> findLockersBySectionId(Long sectionId);

    /**
     * 사물함 한 건. 삭제된 사물함은 없는 것으로 본다.
     */
    Optional<Locker> findLockerById(Long lockerId);

    /**
     * 해당 운영 회차에 이 사물함이 이미 신청되었는지.
     */
    boolean existsApplication(Long lockerPeriodId, Long lockerId);

    /**
     * 해당 운영 회차에 이미 신청된 사물함 식별자.
     */
    Set<Long> findAppliedLockerIds(Long lockerPeriodId);

    /**
     * 해당 운영 회차에서 회원이 신청한 사물함 식별자. 회차당 한 건만 신청할 수 있고 취소가 없어 최대 하나다.
     */
    Optional<Long> findAppliedLockerId(Long lockerPeriodId, Long memberId);

    /**
     * 신청을 저장한다. 같은 회차의 같은 사물함에 먼저 저장된 신청이 있으면
     * {@link kr.ac.kookmin.stream.event.domain.locker.domain.LockerErrorCode#LOCKER_ALREADY_ASSIGNED}로 실패한다.
     */
    LockerApplication saveApplication(LockerApplication application);
}
