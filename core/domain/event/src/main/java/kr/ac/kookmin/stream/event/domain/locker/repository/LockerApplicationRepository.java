package kr.ac.kookmin.stream.event.domain.locker.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplication;

/**
 * 사물함 신청 저장소. 운영 회차·구역·사물함은 {@link LockerRepository}가 맡는다.
 */
public interface LockerApplicationRepository {

    /**
     * 해당 운영 회차에 신청이 한 건이라도 있는지. 신청이 딸린 회차는 지울 수 없다.
     */
    boolean existsByPeriod(Long lockerPeriodId);

    /**
     * 해당 운영 회차에 이 사물함이 이미 신청되었는지.
     */
    boolean existsByLocker(Long lockerPeriodId, Long lockerId);

    /**
     * 해당 운영 회차에 이미 신청된 사물함 식별자.
     */
    Set<Long> findAppliedLockerIds(Long lockerPeriodId);

    /**
     * 해당 운영 회차에 사물함을 가진 회원 식별자. 취소된 신청은 뺀다.
     */
    List<Long> findMemberIds(Long lockerPeriodId);

    /**
     * 해당 운영 회차에서 회원이 쓰고 있는 사물함 식별자. 취소되지 않은 신청은 회차당 한 건까지라 최대 하나다.
     */
    Optional<Long> findAppliedLockerId(Long lockerPeriodId, Long memberId);

    /**
     * 해당 회차들에 이 사물함 중 하나라도 신청됐는지. (회차, 사물함) 유니크 인덱스를 그대로 탄다.
     */
    boolean existsByPeriodIdsAndLockerIds(Collection<Long> lockerPeriodIds, Collection<Long> lockerIds);

    /**
     * 회원의 사물함 신청 전체를 신청 일시 최신순으로 조회한다.
     */
    List<LockerApplication> findByMemberId(Long memberId);

    /**
     * 신청을 저장한다. 같은 회차의 같은 사물함에 먼저 저장된 신청이 있으면
     * {@link kr.ac.kookmin.stream.event.domain.locker.domain.LockerErrorCode#LOCKER_ALREADY_ASSIGNED}로 실패한다.
     */
    LockerApplication save(LockerApplication application);
}
