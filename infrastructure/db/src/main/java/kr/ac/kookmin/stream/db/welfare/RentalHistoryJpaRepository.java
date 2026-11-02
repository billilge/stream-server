package kr.ac.kookmin.stream.db.welfare;

import java.util.Collection;
import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentalHistoryJpaRepository extends JpaRepository<RentalHistoryJpaEntity, Long> {

    @Query("""
        SELECT h FROM RentalHistoryJpaEntity h
        WHERE h.memberId = :memberId
        AND (:status IS NULL OR h.rentalStatus = :status)
        ORDER BY h.appliedAt DESC, h.id DESC
        """)
    List<RentalHistoryJpaEntity> findAllByMemberId(
        @Param("memberId") Long memberId,
        @Param("status") RentalStatus status
    );

    List<RentalHistoryJpaEntity> findAllByRentalStatusIn(Collection<RentalStatus> rentalStatuses);

    boolean existsByItemIdAndMemberIdAndRentalStatusNotIn(Long itemId, Long memberId, Collection<RentalStatus> excludedStatuses);

    boolean existsByItemIdAndRentalStatusNotIn(Long itemId, Collection<RentalStatus> excludedStatuses);

    // 관리자 콘솔용 페이지 번호 조회. memberIds가 null이면 회원 필터를 안 건다.
    @Query(value = """
        SELECT h FROM RentalHistoryJpaEntity h
        WHERE (:status IS NULL OR h.rentalStatus = :status)
        AND (:filterByMember = false OR h.memberId IN :memberIds)
        ORDER BY h.appliedAt DESC, h.id DESC
        """,
        countQuery = """
        SELECT COUNT(h) FROM RentalHistoryJpaEntity h
        WHERE (:status IS NULL OR h.rentalStatus = :status)
        AND (:filterByMember = false OR h.memberId IN :memberIds)
        """)
    Page<RentalHistoryJpaEntity> search(
        @Param("status") RentalStatus status,
        @Param("filterByMember") boolean filterByMember,
        @Param("memberIds") List<Long> memberIds,
        Pageable pageable
    );
}
