package kr.ac.kookmin.stream.db.event;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 취소된 신청은 자리를 비켜준 신청이라 대부분의 조회에서 뺀다. 남겨 두면 취소한 칸을 다시 배정할 수 없다.
 * 예외는 {@link #existsByLockerPeriodId}와 운영진 신청 현황 조회다.
 */
public interface LockerApplicationJpaRepository extends JpaRepository<LockerApplicationJpaEntity, Long> {

    /**
     * 취소된 신청도 센다. 취소 행도 회차를 가리키므로, 회차를 지우면 가리킬 대상이 없어진다.
     */
    boolean existsByLockerPeriodId(Long lockerPeriodId);

    boolean existsByLockerPeriodIdAndLockerIdAndIsCanceledFalse(Long lockerPeriodId, Long lockerId);

    List<LockerApplicationJpaEntity> findAllByMemberIdAndIsCanceledFalseOrderByAppliedAtDescIdDesc(Long memberId);

    /**
     * 해당 운영 회차에 신청된 사물함 식별자. 신청 여부만 필요하므로 신청 자체는 읽지 않는다.
     */
    @Query("""
        SELECT a.lockerId FROM LockerApplicationJpaEntity a
        WHERE a.lockerPeriodId = :lockerPeriodId AND a.isCanceled = false
        """)
    Set<Long> findLockerIdsByLockerPeriodId(@Param("lockerPeriodId") Long lockerPeriodId);

    @Query("""
        SELECT a.memberId FROM LockerApplicationJpaEntity a
        WHERE a.lockerPeriodId = :lockerPeriodId AND a.isCanceled = false
        """)
    List<Long> findMemberIdsByLockerPeriodId(@Param("lockerPeriodId") Long lockerPeriodId);

    boolean existsByLockerPeriodIdInAndLockerIdInAndIsCanceledFalse(
        Collection<Long> lockerPeriodIds, Collection<Long> lockerIds);

    /**
     * 운영진 신청 현황. 취소된 신청도 담아 운영진이 그 칸이 왜 비었는지 알 수 있게 한다.
     */
    @Query(value = """
        SELECT a FROM LockerApplicationJpaEntity a
        WHERE a.lockerPeriodId = :lockerPeriodId
        AND (:filterByMember = false OR a.memberId IN :memberIds)
        ORDER BY a.appliedAt DESC, a.id DESC
        """,
        countQuery = """
        SELECT COUNT(a) FROM LockerApplicationJpaEntity a
        WHERE a.lockerPeriodId = :lockerPeriodId
        AND (:filterByMember = false OR a.memberId IN :memberIds)
        """)
    Page<LockerApplicationJpaEntity> search(
        @Param("lockerPeriodId") Long lockerPeriodId,
        @Param("filterByMember") boolean filterByMember,
        @Param("memberIds") List<Long> memberIds,
        Pageable pageable
    );

    /**
     * 회차당 회원 신청은 유니크 제약으로 한 건까지만 존재한다.
     */
    @Query("""
        SELECT a.lockerId FROM LockerApplicationJpaEntity a
        WHERE a.lockerPeriodId = :lockerPeriodId AND a.memberId = :memberId AND a.isCanceled = false
        """)
    Optional<Long> findLockerIdByLockerPeriodIdAndMemberId(
        @Param("lockerPeriodId") Long lockerPeriodId,
        @Param("memberId") Long memberId
    );
}
