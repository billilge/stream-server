package kr.ac.kookmin.stream.db.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventJpaRepository extends JpaRepository<EventJpaEntity, Long> {

    Optional<EventJpaEntity> findByIdAndIsDeletedFalse(Long id);

    Optional<EventJpaEntity> findByIdAndIsDeletedFalseAndIsPublishedTrue(Long id);

    /**
     * 게시된 행사를 (event_start_at, event_id) keyset 커서로 조회한다.
     * <p>
     * 모집 상태는 저장값이 아니라 강제 마감 여부·신청 기간·잔여 정원으로 판정하므로, 필터가 주어지면
     * {@code Event.calculateRecruitStatus}와 같은 규칙을 SQL로 옮겨 적용한다. 두 곳의 규칙이 어긋나면
     * 필터로 거른 결과와 응답에 실린 모집 상태가 달라지므로, 한쪽을 고치면 다른 쪽도 함께 고쳐야 한다.
     */
    @Query("""
        SELECT e FROM EventJpaEntity e
        WHERE e.isDeleted = false
        AND e.isPublished = true
        AND (:cursorEventStartAt IS NULL
            OR e.eventStartAt > :cursorEventStartAt
            OR (e.eventStartAt = :cursorEventStartAt AND e.id > :cursorEventId))
        AND (:filterAll = true
            OR (:filterBeforeOpen = true
                AND e.recruitStatus <> :forceClosedStatus
                AND :now <= e.applyEndAt
                AND :now < e.applyStartAt)
            OR (:filterOpen = true
                AND e.recruitStatus <> :forceClosedStatus
                AND :now >= e.applyStartAt
                AND :now <= e.applyEndAt
                AND (e.recruitType <> :capacityLimitedType
                    OR (SELECT COUNT(a) FROM EventApplicationJpaEntity a
                        WHERE a.eventId = e.id AND a.status = :appliedStatus) < e.capacity))
            OR (:filterClosed = true
                AND (e.recruitStatus = :forceClosedStatus
                    OR :now > e.applyEndAt
                    OR (:now >= e.applyStartAt
                        AND e.recruitType = :capacityLimitedType
                        AND (SELECT COUNT(a) FROM EventApplicationJpaEntity a
                            WHERE a.eventId = e.id AND a.status = :appliedStatus) >= e.capacity))))
        ORDER BY e.eventStartAt ASC, e.id ASC
        """)
    List<EventJpaEntity> findPublishedSlice(
        @Param("filterAll") boolean filterAll,
        @Param("filterBeforeOpen") boolean filterBeforeOpen,
        @Param("filterOpen") boolean filterOpen,
        @Param("filterClosed") boolean filterClosed,
        @Param("forceClosedStatus") RecruitStatus forceClosedStatus,
        @Param("capacityLimitedType") RecruitType capacityLimitedType,
        @Param("appliedStatus") EventApplicationStatus appliedStatus,
        @Param("now") LocalDateTime now,
        @Param("cursorEventStartAt") LocalDateTime cursorEventStartAt,
        @Param("cursorEventId") Long cursorEventId,
        Pageable pageable
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
