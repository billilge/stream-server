package kr.ac.kookmin.stream.db.welfare;

import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemJpaRepository extends JpaRepository<ItemJpaEntity, Long> {

    // 커서가 없으면 첫 페이지, 있으면 그 다음부터 조회한다. keyword는 이름 부분 일치 검색이다.
    // LIKE 대신 LOCATE를 써서 %·_ 같은 문자도 와일드카드가 아닌 글자 그대로 검색된다
    @Query("""
        SELECT i FROM ItemJpaEntity i
        WHERE (:category IS NULL OR i.category = :category)
        AND (:keyword IS NULL OR LOCATE(:keyword, i.name) > 0)
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
}
