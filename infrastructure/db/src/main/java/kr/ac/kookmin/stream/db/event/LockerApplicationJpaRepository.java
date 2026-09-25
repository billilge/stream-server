package kr.ac.kookmin.stream.db.event;

import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LockerApplicationJpaRepository extends JpaRepository<LockerApplicationJpaEntity, Long> {

    /**
     * 해당 운영 회차에 신청된 사물함 식별자. 신청 여부만 필요하므로 신청 자체는 읽지 않는다.
     */
    @Query("""
        SELECT a.lockerId FROM LockerApplicationJpaEntity a
        WHERE a.lockerPeriodId = :lockerPeriodId
        """)
    Set<Long> findLockerIdsByLockerPeriodId(@Param("lockerPeriodId") Long lockerPeriodId);

    /**
     * 회차당 회원 신청은 유니크 제약으로 한 건까지만 존재한다.
     */
    @Query("""
        SELECT a.lockerId FROM LockerApplicationJpaEntity a
        WHERE a.lockerPeriodId = :lockerPeriodId AND a.memberId = :memberId
        """)
    Optional<Long> findLockerIdByLockerPeriodIdAndMemberId(
        @Param("lockerPeriodId") Long lockerPeriodId,
        @Param("memberId") Long memberId
    );
}
