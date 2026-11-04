package kr.ac.kookmin.stream.db.member;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationJpaRepository extends JpaRepository<NotificationJpaEntity, Long> {

    // 커서가 없으면(cursorId가 null) 첫 페이지, 있으면 그 다음부터 조회한다. 정렬이 createdAt DESC, id DESC라 "다음"은 정렬상 뒤쪽이다
    @Query("""
        SELECT n FROM NotificationJpaEntity n
        WHERE n.memberId = :memberId
        AND (
            :cursorId IS NULL
            OR n.createdAt < :cursorCreatedAt
            OR (n.createdAt = :cursorCreatedAt AND n.id < :cursorId)
        )
        ORDER BY n.createdAt DESC, n.id DESC
        """)
    List<NotificationJpaEntity> findSliceByMemberId(
        @Param("memberId") Long memberId,
        @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
        @Param("cursorId") Long cursorId,
        Pageable pageable
    );

    @Query("""
        SELECT n FROM NotificationJpaEntity n
        WHERE n.memberId IS NULL
        AND (
            :cursorId IS NULL
            OR n.createdAt < :cursorCreatedAt
            OR (n.createdAt = :cursorCreatedAt AND n.id < :cursorId)
        )
        ORDER BY n.createdAt DESC, n.id DESC
        """)
    List<NotificationJpaEntity> findAdminSlice(
        @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
        @Param("cursorId") Long cursorId,
        Pageable pageable
    );

    boolean existsByMemberIdAndReadFalse(Long memberId);

    boolean existsByMemberIdIsNullAndReadFalse();
}
