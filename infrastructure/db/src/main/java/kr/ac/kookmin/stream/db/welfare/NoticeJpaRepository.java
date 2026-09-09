package kr.ac.kookmin.stream.db.welfare;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoticeJpaRepository extends JpaRepository<NoticeJpaEntity, Long> {

    Optional<NoticeJpaEntity> findByIdAndIsDeletedFalse(Long id);

    @Query(
        value = """
            SELECT * FROM notices
            WHERE is_deleted = false
            AND (:category IS NULL OR category = :category)
            ORDER BY pinned DESC, created_at DESC, notice_id DESC
            LIMIT :limit
            """,
        nativeQuery = true
    )
    List<NoticeJpaEntity> findFirstSlice(@Param("category") String category, @Param("limit") int limit);

    @Query(
        value = """
            SELECT * FROM notices
            WHERE is_deleted = false
            AND (:category IS NULL OR category = :category)
            AND (
                pinned < :cursorPinned
                OR (pinned = :cursorPinned AND created_at < :cursorCreatedAt)
                OR (pinned = :cursorPinned AND created_at = :cursorCreatedAt AND notice_id < :cursorId)
            )
            ORDER BY pinned DESC, created_at DESC, notice_id DESC
            LIMIT :limit
            """,
        nativeQuery = true
    )
    List<NoticeJpaEntity> findNextSlice(
        @Param("category") String category,
        @Param("cursorPinned") boolean cursorPinned,
        @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
        @Param("cursorId") Long cursorId,
        @Param("limit") int limit
    );
}
