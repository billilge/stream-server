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

    Optional<FeedbackRoundJpaEntity> findFirstByAnswersPublishedAtIsNullOrderByYearDescRoundDesc();

    @Query("""
        SELECT r FROM FeedbackRoundJpaEntity r
        WHERE (:year IS NULL OR r.year = :year)
        ORDER BY r.year DESC, r.round DESC
        """)
    List<FeedbackRoundJpaEntity> findAllByYear(@Param("year") Integer year);

    // 두 구간은 "내 시작이 상대 마감보다 이르고, 내 마감이 상대 시작보다 늦을 때" 겹친다
    @Query("""
        SELECT COUNT(r) > 0 FROM FeedbackRoundJpaEntity r
        WHERE r.opensAt <= :closesAt AND r.closesAt >= :opensAt
        """)
    boolean existsOverlapping(
        @Param("opensAt") LocalDateTime opensAt, @Param("closesAt") LocalDateTime closesAt);

    // 그 연도에 회차가 없으면 null이 온다
    @Query("SELECT MAX(r.round) FROM FeedbackRoundJpaEntity r WHERE r.year = :year")
    Integer findMaxRoundOf(@Param("year") int year);

    @Query("SELECT DISTINCT r.year FROM FeedbackRoundJpaEntity r ORDER BY r.year DESC")
    List<Integer> findDistinctYears();

    @Query("SELECT r.round FROM FeedbackRoundJpaEntity r WHERE r.year = :year ORDER BY r.round ASC")
    List<Integer> findRoundsByYear(@Param("year") int year);
}
