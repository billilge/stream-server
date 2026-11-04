package kr.ac.kookmin.stream.db.event;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArchiveJpaRepository extends JpaRepository<ArchiveJpaEntity, Long> {

    /**
     * 기간이 주어지면 그 반개구간에 드는 아카이빙만, 없으면 전체를 최신 활동순으로 조회한다.
     * <p>
     * 연도 조건을 {@code YEAR(start_date) = ?}가 아니라 범위로 받는다. 컬럼에 함수를 씌우면 start_date
     * 인덱스를 쓸 수 없어서인데, 현재 archives에는 그 인덱스가 없다. 데이터가 늘어 인덱스를 추가할 때
     * 쿼리를 고치지 않아도 되도록 미리 범위 형태로 둔다.
     */
    @Query("""
        SELECT a FROM ArchiveJpaEntity a
        WHERE a.isDeleted = false
            AND (:startInclusive IS NULL
                OR (a.startDate >= :startInclusive AND a.startDate < :endExclusive))
        ORDER BY a.startDate DESC, a.id DESC
        """)
    List<ArchiveJpaEntity> findAllInPeriod(
        @Param("startInclusive") LocalDate startInclusive,
        @Param("endExclusive") LocalDate endExclusive
    );

    /** 연도 버튼은 목록과 별도로 조회하므로 삭제 필터를 여기에도 둔다. 빠뜨리면 지운 아카이빙의 연도가 남는다. */
    @Query("""
        SELECT DISTINCT YEAR(a.startDate) FROM ArchiveJpaEntity a
        WHERE a.isDeleted = false
        ORDER BY YEAR(a.startDate) DESC
        """)
    List<Integer> findAllYearsDesc();

    Optional<ArchiveJpaEntity> findByIdAndIsDeletedFalse(Long id);

    List<ArchiveJpaEntity> findAllByIdInAndIsDeletedFalse(List<Long> ids);

    /**
     * 운영진 목록. 제목 검색은 {@code LIKE '%...%'}라 인덱스를 타지 못하고 테이블을 훑는다 — 아카이빙이 연간
     * 수십 건 규모라 그대로 둔다. 검색어 이스케이프는 {@code LikePatterns}가 한다.
     */
    @Query(value = """
        SELECT a FROM ArchiveJpaEntity a
        WHERE a.isDeleted = false
        AND (:keyword IS NULL OR a.title LIKE CONCAT('%', :keyword, '%') ESCAPE '!')
        AND (:exposed IS NULL OR a.exposed = :exposed)
        ORDER BY a.createdAt DESC, a.id DESC
        """,
        countQuery = """
        SELECT COUNT(a) FROM ArchiveJpaEntity a
        WHERE a.isDeleted = false
        AND (:keyword IS NULL OR a.title LIKE CONCAT('%', :keyword, '%') ESCAPE '!')
        AND (:exposed IS NULL OR a.exposed = :exposed)
        """)
    Page<ArchiveJpaEntity> search(
        @Param("keyword") String keyword,
        @Param("exposed") Boolean exposed,
        Pageable pageable
    );
}
