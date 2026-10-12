package kr.ac.kookmin.stream.db.welfare;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackRoundJpaRepository extends JpaRepository<FeedbackRoundJpaEntity, Long> {

    @Query("SELECT r FROM FeedbackRoundJpaEntity r WHERE :now BETWEEN r.opensAt AND r.closesAt")
    Optional<FeedbackRoundJpaEntity> findOpenAt(@Param("now") LocalDateTime now);

    @Query("SELECT DISTINCT r.year FROM FeedbackRoundJpaEntity r ORDER BY r.year DESC")
    List<Integer> findDistinctYears();

    @Query("SELECT r.round FROM FeedbackRoundJpaEntity r WHERE r.year = :year ORDER BY r.round ASC")
    List<Integer> findRoundsByYear(@Param("year") int year);
}
