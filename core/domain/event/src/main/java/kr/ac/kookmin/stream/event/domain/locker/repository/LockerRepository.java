package kr.ac.kookmin.stream.event.domain.locker.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriod;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSection;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionLayout;

/**
 * 사물함 운영 회차·구역·사물함 저장소. 사물함 신청은 {@link LockerApplicationRepository}가 맡는다.
 */
public interface LockerRepository {

    /**
     * 운영 회차 전체를 신청 시작 일시 최신순으로 조회한다. 운영진 화면용이라 아직 게시하지 않은 회차도 포함한다.
     */
    List<LockerPeriod> findAllPeriods();

    /**
     * 운영 회차 한 건. 운영진 화면용이라 아직 게시하지 않은 회차도 포함한다.
     */
    Optional<LockerPeriod> findPeriodById(Long lockerPeriodId);

    /**
     * 운영 회차를 저장한다. 식별자가 없으면 새로 만들고, 있으면 그 회차를 바꾼다.
     */
    LockerPeriod savePeriod(LockerPeriod period);

    /**
     * 운영 회차를 소프트 삭제한다. 이미 삭제됐거나 없는 회차면 false를 돌려준다.
     * 변경은 호출한 쪽 트랜잭션이 커밋될 때 반영된다.
     */
    boolean deletePeriod(Long lockerPeriodId);

    /**
     * 게시된 운영 회차가 있는지. 아직 공개하지 않은 회차는 학생에게 없는 것으로 보여야 한다.
     */
    boolean existsPublishedPeriod(Long lockerPeriodId);

    /**
     * 게시된 운영 회차 한 건. 아직 공개하지 않은 회차는 없는 것으로 본다.
     */
    Optional<LockerPeriod> findPublishedPeriodById(Long lockerPeriodId);

    /**
     * 게시된 운영 회차 중 식별자에 해당하는 것. 순서는 보장하지 않는다.
     */
    List<LockerPeriod> findPublishedPeriodsByIds(Collection<Long> lockerPeriodIds);

    /**
     * 전체 구역을 식별자 오름차순으로 조회한다.
     */
    List<LockerSection> findAllSections();

    Optional<LockerSection> findSectionById(Long sectionId);

    /**
     * 구역의 배치 구조와 사진. 아직 등록하지 않은 구역이면 비어 있다.
     */
    Optional<LockerSectionLayout> findLayoutBySectionId(Long sectionId);

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
     * 식별자에 해당하는 사물함. 지난 신청 이력의 사물함 이름을 보여줘야 하므로 삭제된 사물함도 포함한다.
     */
    List<Locker> findLockersByIdsIncludingDeleted(Collection<Long> lockerIds);
}
