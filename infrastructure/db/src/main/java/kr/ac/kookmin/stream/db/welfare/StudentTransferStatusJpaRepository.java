package kr.ac.kookmin.stream.db.welfare;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 납부 요청은 이력으로 쌓인다. 회원의 지금 상태는 PK가 가장 큰 행이다 — 같은 회원에게 반려(REJECTED)가 여러 개
 * 쌓이고 최신 행 자체가 반려일 수도 있어 상태로는 고를 수 없고, created_at은 초 단위라 같은 초에 겹칠 수 있다.
 */
public interface StudentTransferStatusJpaRepository extends JpaRepository<StudentTransferStatusJpaEntity, Long> {

    Optional<StudentTransferStatusJpaEntity> findFirstByMemberIdOrderByIdDesc(Long memberId);

    // 회원별 최신 요청만 대상으로 한다. 상태 필터도 최신 요청의 상태로 건다
    @Query(value = """
        SELECT r FROM StudentTransferStatusJpaEntity r
        WHERE r.id = (SELECT MAX(r2.id) FROM StudentTransferStatusJpaEntity r2 WHERE r2.memberId = r.memberId)
        AND (:status IS NULL OR r.status = :status)
        AND (:filterByMember = false OR r.memberId IN :memberIds)
        ORDER BY r.createdAt DESC, r.id DESC
        """,
        countQuery = """
        SELECT COUNT(r) FROM StudentTransferStatusJpaEntity r
        WHERE r.id = (SELECT MAX(r2.id) FROM StudentTransferStatusJpaEntity r2 WHERE r2.memberId = r.memberId)
        AND (:status IS NULL OR r.status = :status)
        AND (:filterByMember = false OR r.memberId IN :memberIds)
        """)
    Page<StudentTransferStatusJpaEntity> search(
        @Param("status") TransferStatus status,
        @Param("filterByMember") boolean filterByMember,
        @Param("memberIds") List<Long> memberIds,
        Pageable pageable
    );
}
