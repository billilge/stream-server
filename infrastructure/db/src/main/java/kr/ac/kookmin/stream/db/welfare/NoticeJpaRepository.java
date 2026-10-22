package kr.ac.kookmin.stream.db.welfare;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoticeJpaRepository extends JpaRepository<NoticeJpaEntity, Long> {

    Optional<NoticeJpaEntity> findByIdAndIsDeletedFalse(Long id);

    @Query("""
        SELECT n FROM NoticeJpaEntity n
        WHERE n.isDeleted = false
        AND (:category IS NULL OR n.category = :category)
        ORDER BY n.pinned DESC, n.createdAt DESC, n.id DESC
        """)
    List<NoticeJpaEntity> findFirstSlice(@Param("category") NoticeCategory category, Pageable pageable);

    @Query("""
        SELECT n FROM NoticeJpaEntity n
        WHERE n.isDeleted = false
        AND (:category IS NULL OR n.category = :category)
        AND (
            (CASE WHEN n.pinned = true THEN 1 ELSE 0 END) < (CASE WHEN :cursorPinned = true THEN 1 ELSE 0 END)
            OR (n.pinned = :cursorPinned AND n.createdAt < :cursorCreatedAt)
            OR (n.pinned = :cursorPinned AND n.createdAt = :cursorCreatedAt AND n.id < :cursorId)
        )
        ORDER BY n.pinned DESC, n.createdAt DESC, n.id DESC
        """)
    List<NoticeJpaEntity> findNextSlice(
        @Param("category") NoticeCategory category,
        @Param("cursorPinned") boolean cursorPinned,
        @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
        @Param("cursorId") Long cursorId,
        Pageable pageable
    );
}
