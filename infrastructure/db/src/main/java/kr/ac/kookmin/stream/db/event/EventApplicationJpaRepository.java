package kr.ac.kookmin.stream.db.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventApplicationJpaRepository extends JpaRepository<EventApplicationJpaEntity, Long> {

    long countByEventIdAndStatus(Long eventId, EventApplicationStatus status);

    boolean existsByEventId(Long eventId);

    long countByEventId(Long eventId);

    @Query(
        value = """
            SELECT a FROM EventApplicationJpaEntity a
            WHERE a.eventId = :eventId
            AND (:status IS NULL OR a.status = :status)
            AND (:filterByMember = false OR a.memberId IN :memberIds)
            ORDER BY a.appliedAt ASC, a.id ASC
            """,
        countQuery = """
            SELECT COUNT(a) FROM EventApplicationJpaEntity a
            WHERE a.eventId = :eventId
            AND (:status IS NULL OR a.status = :status)
            AND (:filterByMember = false OR a.memberId IN :memberIds)
            """
    )
    Page<EventApplicationJpaEntity> searchByEventId(
        @Param("eventId") Long eventId,
        @Param("status") EventApplicationStatus status,
        @Param("filterByMember") boolean filterByMember,
        @Param("memberIds") List<Long> memberIds,
        Pageable pageable
    );

    boolean existsByEventIdAndMemberIdAndStatus(Long eventId, Long memberId, EventApplicationStatus status);

    Optional<EventApplicationJpaEntity> findByIdAndMemberId(Long id, Long memberId);

    Optional<EventApplicationJpaEntity> findByIdAndEventId(Long id, Long eventId);

    @Query("""
        SELECT a.memberId FROM EventApplicationJpaEntity a
        WHERE a.eventId = :eventId AND a.status = :status
        """)
    List<Long> findMemberIdsByEventIdAndStatus(
        @Param("eventId") Long eventId,
        @Param("status") EventApplicationStatus status
    );

    /**
     * 한 회원의 신청 내역을 (applied_at, event_application_id) keyset 커서로 최신순 조회한다.
     * 취소된 신청도 이력으로 함께 내려간다.
     */
    @Query("""
        SELECT a FROM EventApplicationJpaEntity a
        WHERE a.memberId = :memberId
        AND (:cursorAppliedAt IS NULL
            OR a.appliedAt < :cursorAppliedAt
            OR (a.appliedAt = :cursorAppliedAt AND a.id < :cursorApplicationId))
        ORDER BY a.appliedAt DESC, a.id DESC
        """)
    List<EventApplicationJpaEntity> findSliceByMemberId(
        @Param("memberId") Long memberId,
        @Param("cursorAppliedAt") LocalDateTime cursorAppliedAt,
        @Param("cursorApplicationId") Long cursorApplicationId,
        Pageable pageable
    );

    /**
     * 회원이 신청한 행사 중 아직 지나지 않은 것. 행사와 신청은 FK로 묶여 있지 않아 식별자로 직접 잇는다.
     * 홈 미리보기용이라 정렬은 행사 시작 임박순이고, 당일 행사가 빠지지 않도록 오늘 0시를 기준으로 거른다.
     */
    @Query("""
        SELECT e FROM EventJpaEntity e
        JOIN EventApplicationJpaEntity a ON a.eventId = e.id
        WHERE a.memberId = :memberId
        AND a.status = :appliedStatus
        AND e.isDeleted = false
        AND e.eventStartAt >= :startOfToday
        ORDER BY e.eventStartAt ASC, e.id ASC
        """)
    List<EventJpaEntity> findUpcomingAppliedEvents(
        @Param("memberId") Long memberId,
        @Param("appliedStatus") EventApplicationStatus appliedStatus,
        @Param("startOfToday") LocalDateTime startOfToday,
        Pageable pageable
    );

    @Query("""
        SELECT COUNT(e) FROM EventJpaEntity e
        JOIN EventApplicationJpaEntity a ON a.eventId = e.id
        WHERE a.memberId = :memberId
        AND a.status = :appliedStatus
        AND e.isDeleted = false
        AND e.eventStartAt >= :startOfToday
        """)
    long countUpcomingAppliedEvents(
        @Param("memberId") Long memberId,
        @Param("appliedStatus") EventApplicationStatus appliedStatus,
        @Param("startOfToday") LocalDateTime startOfToday
    );

    @Query("""
        SELECT new kr.ac.kookmin.stream.db.event.EventApplicantCountProjection(a.eventId, COUNT(a))
        FROM EventApplicationJpaEntity a
        WHERE a.eventId IN :eventIds AND a.status = :appliedStatus
        GROUP BY a.eventId
        """)
    List<EventApplicantCountProjection> countApplicantsByEventIds(
        @Param("eventIds") List<Long> eventIds,
        @Param("appliedStatus") EventApplicationStatus appliedStatus
    );
}
