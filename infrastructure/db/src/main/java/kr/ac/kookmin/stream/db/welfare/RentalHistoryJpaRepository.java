package kr.ac.kookmin.stream.db.welfare;

import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;
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

    boolean existsByItemIdAndMemberIdAndRentalStatus(Long itemId, Long memberId, RentalStatus rentalStatus);
}
