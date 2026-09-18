package kr.ac.kookmin.stream.db.event;

import java.time.LocalDate;
import java.util.List;
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
        WHERE (:startInclusive IS NULL
            OR (a.startDate >= :startInclusive AND a.startDate < :endExclusive))
        ORDER BY a.startDate DESC, a.id DESC
        """)
    List<ArchiveJpaEntity> findAllInPeriod(
        @Param("startInclusive") LocalDate startInclusive,
        @Param("endExclusive") LocalDate endExclusive
    );

    @Query("""
        SELECT DISTINCT YEAR(a.startDate) FROM ArchiveJpaEntity a
        ORDER BY YEAR(a.startDate) DESC
        """)
    List<Integer> findAllYearsDesc();
}
