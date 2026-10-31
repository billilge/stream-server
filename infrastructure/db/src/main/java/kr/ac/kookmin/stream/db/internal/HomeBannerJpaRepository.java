package kr.ac.kookmin.stream.db.internal;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HomeBannerJpaRepository extends JpaRepository<HomeBannerJpaEntity, Long> {

    Optional<HomeBannerJpaEntity> findByIdAndIsDeletedFalse(Long id);

    @Query("""
        SELECT b FROM HomeBannerJpaEntity b
        WHERE b.isDeleted = false
        AND b.exposureStartAt <= :at
        AND b.exposureEndAt >= :at
        ORDER BY b.displayOrder ASC, b.id ASC
        """)
    List<HomeBannerJpaEntity> findExposedAt(@Param("at") LocalDateTime at);

    // 노출 중과 노출 예정을 가르지 않고 "끝나지 않은 것"까지만 본다. 둘의 구분은 HomeBanner가 한다 —
    // 같은 판정을 SQL에도 두면 둘이 어긋날 수 있다.
    //
    // 분류 필터는 StudentTransferStatusJpaRepository.search와 같이 boolean 플래그로 켠다.
    @Query("""
        SELECT b FROM HomeBannerJpaEntity b
        WHERE b.isDeleted = false
        AND b.exposureEndAt >= :at
        AND (:filterByType = false OR b.bannerType IN :bannerTypes)
        ORDER BY b.displayOrder ASC, b.id ASC
        """)
    List<HomeBannerJpaEntity> findNotEndedAt(
        @Param("at") LocalDateTime at,
        @Param("filterByType") boolean filterByType,
        @Param("bannerTypes") Collection<HomeBannerType> bannerTypes
    );

    @Query(value = """
        SELECT b FROM HomeBannerJpaEntity b
        WHERE b.isDeleted = false
        AND b.exposureEndAt < :at
        AND (:filterByType = false OR b.bannerType IN :bannerTypes)
        ORDER BY b.exposureEndAt DESC, b.id DESC
        """,
        countQuery = """
        SELECT COUNT(b) FROM HomeBannerJpaEntity b
        WHERE b.isDeleted = false
        AND b.exposureEndAt < :at
        AND (:filterByType = false OR b.bannerType IN :bannerTypes)
        """)
    Page<HomeBannerJpaEntity> findEndedBefore(
        @Param("at") LocalDateTime at,
        @Param("filterByType") boolean filterByType,
        @Param("bannerTypes") Collection<HomeBannerType> bannerTypes,
        Pageable pageable
    );
}
