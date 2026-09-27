package kr.ac.kookmin.stream.db.welfare;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemJpaRepository extends JpaRepository<ItemJpaEntity, Long> {

    // 대여 신청의 재고 차감용. 비관적 락으로 읽어서, 같은 물품에 동시에 들어온 신청이 서로 다른
    // 트랜잭션의 커밋을 기다리게 한다(그냥 SELECT는 락이 없어 두 요청이 같은 재고를 동시에 통과할 수 있다).
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM ItemJpaEntity i WHERE i.id = :id")
    Optional<ItemJpaEntity> findByIdForUpdate(@Param("id") Long id);

    // keywordPattern은 LIKE 패턴이다. 검색어의 %·_를 이스케이프한 뒤 '!'를 이스케이프 문자로 쓴다
    // ('\\'는 MySQL 문자열 리터럴에서 다시 이스케이프되어 쓸 수 없다)
    @Query("""
        SELECT i FROM ItemJpaEntity i
        WHERE (:category IS NULL OR i.category = :category)
        AND (:keywordPattern IS NULL OR i.name LIKE :keywordPattern ESCAPE '!')
        ORDER BY i.name ASC, i.id ASC
        """)
    List<ItemJpaEntity> findFirstSlice(
        @Param("category") ItemCategory category,
        @Param("keywordPattern") String keywordPattern,
        Pageable pageable
    );

    @Query("""
        SELECT i FROM ItemJpaEntity i
        WHERE (:category IS NULL OR i.category = :category)
        AND (:keywordPattern IS NULL OR i.name LIKE :keywordPattern ESCAPE '!')
        AND (i.name > :cursorName OR (i.name = :cursorName AND i.id > :cursorId))
        ORDER BY i.name ASC, i.id ASC
        """)
    List<ItemJpaEntity> findNextSlice(
        @Param("category") ItemCategory category,
        @Param("keywordPattern") String keywordPattern,
        @Param("cursorName") String cursorName,
        @Param("cursorId") Long cursorId,
        Pageable pageable
    );
}
