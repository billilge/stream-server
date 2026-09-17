package kr.ac.kookmin.stream.db.welfare;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentFeeJpaRepository extends JpaRepository<StudentFeeJpaEntity, Long> {

    Optional<StudentFeeJpaEntity> findByMemberId(Long memberId);

    @Query(value = """
        SELECT f FROM StudentFeeJpaEntity f
        WHERE (:status IS NULL OR f.status = :status)
        AND (:filterByMember = false OR f.memberId IN :memberIds)
        ORDER BY f.createdAt DESC, f.id DESC
        """,
        countQuery = """
        SELECT COUNT(f) FROM StudentFeeJpaEntity f
        WHERE (:status IS NULL OR f.status = :status)
        AND (:filterByMember = false OR f.memberId IN :memberIds)
        """)
    Page<StudentFeeJpaEntity> search(
        @Param("status") TransferStatus status,
        @Param("filterByMember") boolean filterByMember,
        @Param("memberIds") List<Long> memberIds,
        Pageable pageable
    );
}
