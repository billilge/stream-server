package kr.ac.kookmin.stream.db.event;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventJpaRepository extends JpaRepository<EventJpaEntity, Long> {

    Optional<EventJpaEntity> findByIdAndIsDeletedFalse(Long id);

    Optional<EventJpaEntity> findByIdAndIsDeletedFalseAndIsPublishedTrue(Long id);

    List<EventJpaEntity> findAllByIdInAndIsDeletedFalseAndIsPublishedTrue(Collection<Long> ids);

    @Query("""
        SELECT e FROM EventJpaEntity e
        WHERE e.isDeleted = false
        AND e.isPublished = true
        AND e.applyEndAt >= :at
        """)
    List<EventJpaEntity> findPublishedApplyNotEndedAt(@Param("at") LocalDateTime at);

    /**
     * 삭제되지 않은 행사를 공개 여부와 상관없이 최근 등록 순으로 페이지 조회한다. 운영진 목록용이다.
     * <p>
     * keyword는 LikePatterns.escape로 이스케이프한 검색어다. 쿼리의 ESCAPE 문자는 LikePatterns.ESCAPE와 같아야 한다.
     */
    @Query(
        value = """
            SELECT e FROM EventJpaEntity e
            WHERE e.isDeleted = false
            AND (:keyword IS NULL OR e.title LIKE CONCAT('%', :keyword, '%') ESCAPE '!')
            ORDER BY e.id DESC
            """,
        countQuery = """
            SELECT COUNT(e) FROM EventJpaEntity e
            WHERE e.isDeleted = false
            AND (:keyword IS NULL OR e.title LIKE CONCAT('%', :keyword, '%') ESCAPE '!')
            """
    )
    Page<EventJpaEntity> search(@Param("keyword") String keyword, Pageable pageable);

    // updated_at은 엔티티에서 읽기 전용(updatable = false)이라 네이티브 쿼리로 직접 올린다
    @Modifying
    @Query(value = "UPDATE events SET updated_at = CURRENT_TIMESTAMP WHERE event_id = :eventId", nativeQuery = true)
    void touchUpdatedAt(@Param("eventId") Long eventId);

    /*
     * 게시된 행사를 모집 상태 구간 하나씩 (정렬 키, event_id) keyset 커서로 조회한다. 구간마다 정렬 키와 방향이 달라
     * 쿼리를 나눈다.
     *
     * 모집 상태는 저장값이 아니라 신청 기간·잔여 정원으로 판정하므로 Event.calculateRecruitStatus와 같은 규칙을
     * SQL로 옮겨 적용한다. 두 곳의 규칙이 어긋나면 거른 결과와 응답에 실린 모집 상태가 달라지고, 응답의 상태로
     * 만든 다음 커서가 엉뚱한 구간을 가리키므로 한쪽을 고치면 다른 쪽도 함께 고쳐야 한다.
     *
     * keyword는 LikePatterns.escape로 이스케이프한 제목 검색어다. 쿼리의 ESCAPE 문자는 LikePatterns.ESCAPE와 같아야 한다.
     */

    /** 모집 중인 행사. 신청 마감 임박순(apply_end_at 오름차순). */
    @Query("""
        SELECT e FROM EventJpaEntity e
        WHERE e.isDeleted = false
        AND e.isPublished = true
        AND (:keyword IS NULL OR e.title LIKE CONCAT('%', :keyword, '%') ESCAPE '!')
        AND :now >= e.applyStartAt
        AND :now <= e.applyEndAt
        AND (e.recruitType <> :capacityLimitedType
            OR (SELECT COUNT(a) FROM EventApplicationJpaEntity a
                WHERE a.eventId = e.id AND a.status = :appliedStatus) < e.capacity)
        AND (:cursorSortAt IS NULL
            OR e.applyEndAt > :cursorSortAt
            OR (e.applyEndAt = :cursorSortAt AND e.id > :cursorEventId))
        ORDER BY e.applyEndAt ASC, e.id ASC
        """)
    List<EventJpaEntity> findOpenSlice(
        @Param("capacityLimitedType") RecruitType capacityLimitedType,
        @Param("appliedStatus") EventApplicationStatus appliedStatus,
        @Param("now") LocalDateTime now,
        @Param("keyword") String keyword,
        @Param("cursorSortAt") LocalDateTime cursorSortAt,
        @Param("cursorEventId") Long cursorEventId,
        Pageable pageable
    );

    /** 모집 예정인 행사. 신청 시작 임박순(apply_start_at 오름차순). */
    @Query("""
        SELECT e FROM EventJpaEntity e
        WHERE e.isDeleted = false
        AND e.isPublished = true
        AND (:keyword IS NULL OR e.title LIKE CONCAT('%', :keyword, '%') ESCAPE '!')
        AND :now <= e.applyEndAt
        AND :now < e.applyStartAt
        AND (:cursorSortAt IS NULL
            OR e.applyStartAt > :cursorSortAt
            OR (e.applyStartAt = :cursorSortAt AND e.id > :cursorEventId))
        ORDER BY e.applyStartAt ASC, e.id ASC
        """)
    List<EventJpaEntity> findBeforeOpenSlice(
        @Param("now") LocalDateTime now,
        @Param("keyword") String keyword,
        @Param("cursorSortAt") LocalDateTime cursorSortAt,
        @Param("cursorEventId") Long cursorEventId,
        Pageable pageable
    );

    /** 모집이 끝난 행사(신청 기간이 지났거나 선착순 정원이 참). 최근 행사순(event_start_at 내림차순). */
    @Query("""
        SELECT e FROM EventJpaEntity e
        WHERE e.isDeleted = false
        AND e.isPublished = true
        AND (:keyword IS NULL OR e.title LIKE CONCAT('%', :keyword, '%') ESCAPE '!')
        AND (:now > e.applyEndAt
            OR (:now >= e.applyStartAt
                AND e.recruitType = :capacityLimitedType
                AND (SELECT COUNT(a) FROM EventApplicationJpaEntity a
                    WHERE a.eventId = e.id AND a.status = :appliedStatus) >= e.capacity))
        AND (:cursorSortAt IS NULL
            OR e.eventStartAt < :cursorSortAt
            OR (e.eventStartAt = :cursorSortAt AND e.id < :cursorEventId))
        ORDER BY e.eventStartAt DESC, e.id DESC
        """)
    List<EventJpaEntity> findClosedSlice(
        @Param("capacityLimitedType") RecruitType capacityLimitedType,
        @Param("appliedStatus") EventApplicationStatus appliedStatus,
        @Param("now") LocalDateTime now,
        @Param("keyword") String keyword,
        @Param("cursorSortAt") LocalDateTime cursorSortAt,
        @Param("cursorEventId") Long cursorEventId,
        Pageable pageable
    );

    /**
     * 게시된 행사 중 제목에 검색어가 들어 있는 것의 수. 모집 상태로는 거르지 않으므로 위 세 구간 쿼리를 합친 수와 같다.
     * <p>
     * keyword는 LikePatterns.escape로 이스케이프한 제목 검색어다. 쿼리의 ESCAPE 문자는 LikePatterns.ESCAPE와 같아야 한다.
     */
    @Query("""
        SELECT COUNT(e) FROM EventJpaEntity e
        WHERE e.isDeleted = false
        AND e.isPublished = true
        AND (:keyword IS NULL OR e.title LIKE CONCAT('%', :keyword, '%') ESCAPE '!')
        """)
    long countPublished(@Param("keyword") String keyword);

    @Query("""
        SELECT e FROM EventJpaEntity e
        WHERE e.isDeleted = false
        AND e.isPublished = true
        AND e.eventStartAt >= :from
        AND e.eventStartAt < :to
        """)
    List<EventJpaEntity> findPublishedStartingBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
        SELECT e FROM EventJpaEntity e
        WHERE e.isDeleted = false
        AND e.isPublished = true
        AND e.applyEndAt >= :from
        AND e.applyEndAt < :to
        """)
    List<EventJpaEntity> findPublishedApplyEndingBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    /**
     * 회원이 신청한 행사 중 아직 끝나지 않은 것. 행사와 신청은 FK로 묶여 있지 않아 식별자로 직접 잇는다.
     * 홈 미리보기용이라 정렬은 행사 시작 임박순이다.
     * <p>
     * 신청을 JOIN하지 않고 {@code EXISTS}로 거른다. (행사, 회원) 유니크가 없어 유효 신청이 두 건 생길 수 있는데,
     * JOIN이면 같은 행사가 신청 수만큼 복제된다. 신청했는지만 알면 되므로 존재 여부로 본다.
     * <p>
     * 종료 일시로 거르므로 여러 날에 걸친 행사는 마지막 날까지 남는다. {@code event_end_at}이 NULL인 기존 행은
     * 시작 일시로 대신한다.
     *
     * @param startOfToday 오늘 0시. 시각이 아니라 날짜로 거른다 — 오늘 끝나는 행사는 이미 지났어도 보여줘야 한다
     */
    @Query(
        value = """
            SELECT e FROM EventJpaEntity e
            WHERE e.isDeleted = false
            AND COALESCE(e.eventEndAt, e.eventStartAt) >= :startOfToday
            AND EXISTS (
                SELECT a.id FROM EventApplicationJpaEntity a
                WHERE a.eventId = e.id
                AND a.memberId = :memberId
                AND a.status = :appliedStatus)
            ORDER BY e.eventStartAt ASC, e.id ASC
            """,
        countQuery = """
            SELECT COUNT(e) FROM EventJpaEntity e
            WHERE e.isDeleted = false
            AND COALESCE(e.eventEndAt, e.eventStartAt) >= :startOfToday
            AND EXISTS (
                SELECT a.id FROM EventApplicationJpaEntity a
                WHERE a.eventId = e.id
                AND a.memberId = :memberId
                AND a.status = :appliedStatus)
            """
    )
    Page<EventJpaEntity> findUpcomingAppliedEvents(
        @Param("memberId") Long memberId,
        @Param("appliedStatus") EventApplicationStatus appliedStatus,
        @Param("startOfToday") LocalDateTime startOfToday,
        Pageable pageable
    );
}
