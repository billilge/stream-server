package kr.ac.kookmin.stream.db.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventApplicationJpaRepository extends JpaRepository<EventApplicationJpaEntity, Long> {

    long countByEventIdAndStatus(Long eventId, EventApplicationStatus status);

    boolean existsByEventIdAndMemberIdAndStatus(Long eventId, Long memberId, EventApplicationStatus status);

    Optional<EventApplicationJpaEntity> findByIdAndMemberId(Long id, Long memberId);

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
}
