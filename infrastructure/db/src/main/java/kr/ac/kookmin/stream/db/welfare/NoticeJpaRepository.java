package kr.ac.kookmin.stream.db.welfare;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoticeJpaRepository extends JpaRepository<NoticeJpaEntity, Long> {

    Optional<NoticeJpaEntity> findByIdAndIsDeletedFalse(Long id);

    // LIKE 대신 LOCATE를 써서 %·_ 같은 문자도 와일드카드가 아닌 글자 그대로 검색된다
    @Query(value = """
        SELECT n FROM NoticeJpaEntity n
        WHERE n.isDeleted = false
        AND (:keyword IS NULL OR LOCATE(:keyword, n.title) > 0)
        ORDER BY n.pinned DESC, n.createdAt DESC, n.id DESC
        """,
        countQuery = """
        SELECT COUNT(n) FROM NoticeJpaEntity n
        WHERE n.isDeleted = false
        AND (:keyword IS NULL OR LOCATE(:keyword, n.title) > 0)
        """)
    Page<NoticeJpaEntity> search(@Param("keyword") String keyword, Pageable pageable);

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
