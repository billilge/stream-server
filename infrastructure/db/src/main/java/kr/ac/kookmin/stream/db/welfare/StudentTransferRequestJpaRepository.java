package kr.ac.kookmin.stream.db.welfare;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentTransferRequestJpaRepository extends JpaRepository<StudentTransferRequestJpaEntity, Long> {

    Optional<StudentTransferRequestJpaEntity> findByMemberId(Long memberId);

    @Query(value = """
        SELECT r FROM StudentTransferRequestJpaEntity r
        WHERE (:status IS NULL OR r.status = :status)
        AND (:filterByMember = false OR r.memberId IN :memberIds)
        ORDER BY r.createdAt DESC, r.id DESC
        """,
        countQuery = """
        SELECT COUNT(r) FROM StudentTransferRequestJpaEntity r
        WHERE (:status IS NULL OR r.status = :status)
        AND (:filterByMember = false OR r.memberId IN :memberIds)
        """)
    Page<StudentTransferRequestJpaEntity> search(
        @Param("status") TransferStatus status,
        @Param("filterByMember") boolean filterByMember,
        @Param("memberIds") List<Long> memberIds,
        Pageable pageable
    );
}
