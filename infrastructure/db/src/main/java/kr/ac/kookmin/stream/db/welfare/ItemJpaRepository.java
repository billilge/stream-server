package kr.ac.kookmin.stream.db.welfare;

import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemJpaRepository extends JpaRepository<ItemJpaEntity, Long> {

    // keywordPattern은 LIKE 패턴이다. 검색어의 %·_를 이스케이프한 뒤 '!'를 이스케이프 문자로 쓴다
    // ('\\'는 MySQL 문자열 리터럴에서 다시 이스케이프되어 쓸 수 없다)
    // 커서가 없으면 첫 페이지, 있으면 그 다음부터 조회한다
    @Query("""
        SELECT i FROM ItemJpaEntity i
        WHERE (:category IS NULL OR i.category = :category)
        AND (:keywordPattern IS NULL OR i.name LIKE :keywordPattern ESCAPE '!')
        AND (:cursorName IS NULL OR i.name > :cursorName OR (i.name = :cursorName AND i.id > :cursorId))
        ORDER BY i.name ASC, i.id ASC
        """)
    List<ItemJpaEntity> findSlice(
        @Param("category") ItemCategory category,
        @Param("keywordPattern") String keywordPattern,
        @Param("cursorName") String cursorName,
        @Param("cursorId") Long cursorId,
        Pageable pageable
    );
}
