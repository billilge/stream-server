package kr.ac.kookmin.stream.db.welfare;

import java.util.Collection;
import java.util.List;
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

    @Query("""
        SELECT f FROM FeedbackJpaEntity f
        WHERE f.isDeleted = false AND f.year = :year AND f.round = :round
        ORDER BY f.questionedAt ASC, f.id ASC
        """)
    List<FeedbackJpaEntity> findAllByYearAndRound(@Param("year") int year, @Param("round") int round);

    // createdBy는 welfare 자기 테이블의 숫자 컬럼이라 member 테이블과 조인하지 않는다.
    // 이름으로 찾은 회원 ID를 호출부가 넘겨주고 여기서는 IN으로만 비교한다.
    // keyword는 LikePatterns.escape로 이스케이프한 검색어다. 쿼리의 ESCAPE 문자는 LikePatterns.ESCAPE와 같아야 한다
    @Query(value = """
        SELECT f FROM FeedbackJpaEntity f
        WHERE f.isDeleted = false AND f.year = :year AND f.round = :round
        AND (:keyword IS NULL
             OR f.question LIKE CONCAT('%', :keyword, '%') ESCAPE '!'
             OR f.createdBy IN :memberIds)
        ORDER BY f.questionedAt DESC, f.id DESC
        """,
        countQuery = """
        SELECT COUNT(f) FROM FeedbackJpaEntity f
        WHERE f.isDeleted = false AND f.year = :year AND f.round = :round
        AND (:keyword IS NULL
             OR f.question LIKE CONCAT('%', :keyword, '%') ESCAPE '!'
             OR f.createdBy IN :memberIds)
        """)
    Page<FeedbackJpaEntity> searchByKeyword(
        @Param("year") int year,
        @Param("round") int round,
        @Param("keyword") String keyword,
        @Param("memberIds") Collection<Long> memberIds,
        Pageable pageable
    );

    // COUNT(f.answer)는 answer가 NULL인 행을 세지 않으므로 그대로 "답변이 작성된 수"가 된다
    @Query("""
        SELECT f.year AS year, f.round AS round,
               COUNT(f) AS questionCount, COUNT(f.answer) AS answerCount
        FROM FeedbackJpaEntity f
        WHERE f.isDeleted = false AND (:year IS NULL OR f.year = :year)
        GROUP BY f.year, f.round
        """)
    List<FeedbackCountRow> findCountsByYear(@Param("year") Integer year);
}
