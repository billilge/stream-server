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

    // 커서가 없으면(cursorId가 null) 첫 페이지, 있으면 그 다음부터 조회한다. 커서의 세 값(pinned, createdAt, id)은 함께 넘긴다.
    // 정렬이 pinned DESC, createdAt DESC, id DESC라 "다음"은 정렬상 뒤쪽이다. boolean은 0/1로 바꿔 비교한다
    @Query("""
        SELECT n FROM NoticeJpaEntity n
        WHERE n.isDeleted = false
        AND (:category IS NULL OR n.category = :category)
        AND (
            :cursorId IS NULL
            OR (CASE WHEN n.pinned = true THEN 1 ELSE 0 END) < (CASE WHEN :cursorPinned = true THEN 1 ELSE 0 END)
            OR (n.pinned = :cursorPinned AND n.createdAt < :cursorCreatedAt)
            OR (n.pinned = :cursorPinned AND n.createdAt = :cursorCreatedAt AND n.id < :cursorId)
        )
        ORDER BY n.pinned DESC, n.createdAt DESC, n.id DESC
        """)
    List<NoticeJpaEntity> findSlice(
        @Param("category") NoticeCategory category,
        @Param("cursorPinned") Boolean cursorPinned,
        @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
        @Param("cursorId") Long cursorId,
        Pageable pageable
    );
}
