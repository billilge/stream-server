package kr.ac.kookmin.stream.db.welfare;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OpenFeedbackJpaRepository extends JpaRepository<OpenFeedbackJpaEntity, Long> {

    Optional<OpenFeedbackJpaEntity> findByIdAndIsDeletedFalse(Long id);

    @Query(value = """
        SELECT f FROM OpenFeedbackJpaEntity f
        WHERE f.isDeleted = false
        AND (:year IS NULL OR f.year = :year)
        AND (:round IS NULL OR f.round = :round)
        ORDER BY f.questionedAt DESC, f.id DESC
        """,
        countQuery = """
        SELECT COUNT(f) FROM OpenFeedbackJpaEntity f
        WHERE f.isDeleted = false
        AND (:year IS NULL OR f.year = :year)
        AND (:round IS NULL OR f.round = :round)
        """)
    Page<OpenFeedbackJpaEntity> search(@Param("year") Integer year, @Param("round") Integer round, Pageable pageable);
}
