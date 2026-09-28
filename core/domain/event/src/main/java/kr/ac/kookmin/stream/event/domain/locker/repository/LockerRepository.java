package kr.ac.kookmin.stream.event.domain.locker.repository;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriod;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSection;

/**
 * 사물함 운영 회차·구역·사물함 저장소. 사물함 신청은 {@link LockerApplicationRepository}가 맡는다.
 */
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
}
