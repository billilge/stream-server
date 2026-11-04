package kr.ac.kookmin.stream.db.welfare;

import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCategory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemJpaRepository extends JpaRepository<ItemJpaEntity, Long> {

    // 커서가 없으면 첫 페이지, 있으면 그 다음부터 조회한다. keyword는 이름 부분 일치 검색이며 LikePatterns.escape로
    // 이스케이프한 값을 받는다. 쿼리의 ESCAPE 문자는 LikePatterns.ESCAPE와 같아야 한다.
    // 조건·정렬을 바꾸면 통합 검색 미리보기(findSearchPreview)도 함께 고친다
    @Query("""
        SELECT i FROM ItemJpaEntity i
        WHERE (:category IS NULL OR i.category = :category)
        AND (:keyword IS NULL OR i.name LIKE CONCAT('%', :keyword, '%') ESCAPE '!')
        AND (:cursorName IS NULL OR i.name > :cursorName OR (i.name = :cursorName AND i.id > :cursorId))
        ORDER BY i.name ASC, i.id ASC
        """)
    List<ItemJpaEntity> findSlice(
        @Param("category") ItemCategory category,
        @Param("keyword") String keyword,
        @Param("cursorName") String cursorName,
        @Param("cursorId") Long cursorId,
        Pageable pageable
    );

    // 학생 앱 통합 검색 미리보기. 미리보기 다음은 "더보기"로 학생 목록(findSlice)을 이어 보므로, findSlice를 카테고리·커서
    // 없이 쓴 것과 조건·정렬이 같아야 한다. keyword는 LikePatterns.escape로 이스케이프한 이름 검색어다
    @Query(value = """
        SELECT i FROM ItemJpaEntity i
        WHERE (:keyword IS NULL OR i.name LIKE CONCAT('%', :keyword, '%') ESCAPE '!')
        ORDER BY i.name ASC, i.id ASC
        """,
        countQuery = """
        SELECT COUNT(i) FROM ItemJpaEntity i
        WHERE (:keyword IS NULL OR i.name LIKE CONCAT('%', :keyword, '%') ESCAPE '!')
        """)
    Page<ItemJpaEntity> findSearchPreview(@Param("keyword") String keyword, Pageable pageable);

    // 관리자 콘솔용 페이지 번호 조회. keyword는 LikePatterns.escape로 이스케이프한 값을 받는다
    @Query(value = """
        SELECT i FROM ItemJpaEntity i
        WHERE (:category IS NULL OR i.category = :category)
        AND (:type IS NULL OR i.type = :type)
        AND (:keyword IS NULL OR i.name LIKE CONCAT('%', :keyword, '%') ESCAPE '!')
        ORDER BY i.name ASC, i.id ASC
        """,
        countQuery = """
        SELECT COUNT(i) FROM ItemJpaEntity i
        WHERE (:category IS NULL OR i.category = :category)
        AND (:type IS NULL OR i.type = :type)
        AND (:keyword IS NULL OR i.name LIKE CONCAT('%', :keyword, '%') ESCAPE '!')
        """)
    Page<ItemJpaEntity> search(
        @Param("category") ItemCategory category,
        @Param("type") ItemType type,
        @Param("keyword") String keyword,
        Pageable pageable
    );

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
