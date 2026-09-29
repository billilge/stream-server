package kr.ac.kookmin.stream.db.welfare;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackJpaRepository extends JpaRepository<FeedbackJpaEntity, Long> {

    Optional<FeedbackJpaEntity> findByIdAndIsDeletedFalse(Long id);

    @Query(value = """
        SELECT f FROM FeedbackJpaEntity f
        WHERE f.isDeleted = false
        AND (:year IS NULL OR f.year = :year)
        AND (:round IS NULL OR f.round = :round)
        ORDER BY f.questionedAt DESC, f.id DESC
        """,
        countQuery = """
        SELECT COUNT(f) FROM FeedbackJpaEntity f
        WHERE f.isDeleted = false
        AND (:year IS NULL OR f.year = :year)
        AND (:round IS NULL OR f.round = :round)
        """)
    Page<FeedbackJpaEntity> search(@Param("year") Integer year, @Param("round") Integer round, Pageable pageable);
}
