package kr.ac.kookmin.stream.db.welfare;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PayerJpaRepository extends JpaRepository<PayerJpaEntity, Long> {
    Optional<PayerJpaEntity> findByMemberId(Long memberId);
    void deleteByMemberId(Long memberId);

    // keyword는 LikePatterns.escape로 이스케이프한 이름·학번 검색어다. 쿼리의 ESCAPE 문자는 LikePatterns.ESCAPE와 같아야 한다
    @Query(value = """
        SELECT p FROM PayerJpaEntity p
        WHERE (:keyword IS NULL
            OR p.name LIKE CONCAT('%', :keyword, '%') ESCAPE '!'
            OR p.studentId LIKE CONCAT('%', :keyword, '%') ESCAPE '!')
        ORDER BY p.name ASC, p.id ASC
        """,
        countQuery = """
        SELECT COUNT(p) FROM PayerJpaEntity p
        WHERE (:keyword IS NULL
            OR p.name LIKE CONCAT('%', :keyword, '%') ESCAPE '!'
            OR p.studentId LIKE CONCAT('%', :keyword, '%') ESCAPE '!')
        """)
    Page<PayerJpaEntity> search(@Param("keyword") String keyword, Pageable pageable);
}
