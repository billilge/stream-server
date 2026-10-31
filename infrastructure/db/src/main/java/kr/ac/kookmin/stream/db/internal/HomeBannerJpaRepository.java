package kr.ac.kookmin.stream.db.internal;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HomeBannerJpaRepository extends JpaRepository<HomeBannerJpaEntity, Long> {

    @Query("""
        SELECT b FROM HomeBannerJpaEntity b
        WHERE b.isDeleted = false
        AND b.exposureStartAt <= :at
        AND b.exposureEndAt >= :at
        ORDER BY b.displayOrder ASC, b.id ASC
        """)
    List<HomeBannerJpaEntity> findExposedAt(@Param("at") LocalDateTime at);
}
