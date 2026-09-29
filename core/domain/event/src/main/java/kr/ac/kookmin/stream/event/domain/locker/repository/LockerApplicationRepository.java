package kr.ac.kookmin.stream.event.domain.locker.repository;

import java.util.Optional;
import java.util.Set;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplication;

/**
 * 사물함 신청 저장소. 운영 회차·구역·사물함은 {@link LockerRepository}가 맡는다.
 */
public interface LockerApplicationRepository {

    /**
     * 해당 운영 회차에 이 사물함이 이미 신청되었는지.
     */
    boolean existsByLocker(Long lockerPeriodId, Long lockerId);

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
    LockerApplication save(LockerApplication application);
}
