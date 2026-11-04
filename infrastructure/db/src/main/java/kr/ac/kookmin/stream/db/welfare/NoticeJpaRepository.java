package kr.ac.kookmin.stream.db.welfare;

import java.time.LocalDateTime;
import java.util.Collection;
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

    List<NoticeJpaEntity> findAllByIdInAndIsDeletedFalse(Collection<Long> ids);

    // keyword는 LikePatterns.escape로 이스케이프한 제목 검색어다. 쿼리의 ESCAPE 문자는 LikePatterns.ESCAPE와 같아야 한다
    @Query(value = """
        SELECT n FROM NoticeJpaEntity n
        WHERE n.isDeleted = false
        AND (:category IS NULL OR n.category = :category)
        AND (:keyword IS NULL OR n.title LIKE CONCAT('%', :keyword, '%') ESCAPE '!')
        ORDER BY n.pinned DESC, n.createdAt DESC, n.id DESC
        """,
        countQuery = """
        SELECT COUNT(n) FROM NoticeJpaEntity n
        WHERE n.isDeleted = false
        AND (:category IS NULL OR n.category = :category)
        AND (:keyword IS NULL OR n.title LIKE CONCAT('%', :keyword, '%') ESCAPE '!')
        """)
    Page<NoticeJpaEntity> search(
        @Param("category") NoticeCategory category,
        @Param("keyword") String keyword,
        Pageable pageable
    );

    // 커서가 없으면(cursorId가 null) 첫 페이지, 있으면 그 다음부터 조회한다. 커서의 세 값(pinned, createdAt, id)은 함께 넘긴다.
    // 정렬이 pinned DESC, createdAt DESC, id DESC라 "다음"은 정렬상 뒤쪽이다. boolean은 0/1로 바꿔 비교한다.
    // keyword는 LikePatterns.escape로 이스케이프한 제목 검색어다. 쿼리의 ESCAPE 문자는 LikePatterns.ESCAPE와 같아야 한다.
    // 조건·정렬을 바꾸면 통합 검색 미리보기(findSearchPreview)도 함께 고친다
    @Query("""
        SELECT n FROM NoticeJpaEntity n
        WHERE n.isDeleted = false
        AND (:category IS NULL OR n.category = :category)
        AND (:keyword IS NULL OR n.title LIKE CONCAT('%', :keyword, '%') ESCAPE '!')
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
        @Param("keyword") String keyword,
        @Param("cursorPinned") Boolean cursorPinned,
        @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
        @Param("cursorId") Long cursorId,
        Pageable pageable
    );

    // 학생 앱 통합 검색 미리보기. 미리보기 다음은 "더보기"로 학생 목록(findSlice)을 이어 보므로, findSlice를 종류·커서 없이
    // 쓴 것과 조건·정렬이 같아야 한다. keyword는 LikePatterns.escape로 이스케이프한 제목 검색어다
    @Query(value = """
        SELECT n FROM NoticeJpaEntity n
        WHERE n.isDeleted = false
        AND (:keyword IS NULL OR n.title LIKE CONCAT('%', :keyword, '%') ESCAPE '!')
        ORDER BY n.pinned DESC, n.createdAt DESC, n.id DESC
        """,
        countQuery = """
        SELECT COUNT(n) FROM NoticeJpaEntity n
        WHERE n.isDeleted = false
        AND (:keyword IS NULL OR n.title LIKE CONCAT('%', :keyword, '%') ESCAPE '!')
        """)
    Page<NoticeJpaEntity> findSearchPreview(@Param("keyword") String keyword, Pageable pageable);
}
