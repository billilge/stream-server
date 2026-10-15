package kr.ac.kookmin.stream.db.member;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberJpaRepository extends JpaRepository<MemberJpaEntity, Long> {

    Optional<MemberJpaEntity> findByIdAndIsDeletedFalse(Long id);

    Optional<MemberJpaEntity> findByStudentIdAndIsDeletedFalse(String studentId);

    boolean existsByPhoneNumberAndIsDeletedFalse(String phoneNumber);

    List<MemberJpaEntity> findAllByIdInAndIsDeletedFalse(List<Long> ids);

    @Query("""
        SELECT m.id FROM MemberJpaEntity m
        WHERE m.isDeleted = false
        AND (m.name LIKE CONCAT('%', :keyword, '%') OR m.studentId LIKE CONCAT('%', :keyword, '%'))
        """)
    List<Long> searchIdsByKeyword(@Param("keyword") String keyword);

    // 관리자 납부자 목록은 납부 정보를 내지 않은 회원도 보여줘야 해서 회원 기준으로 조회한다.
    // feeStatus 필터는 idx_members_fee_status_is_deleted(V20)를 탄다.
    @Query("""
        SELECT m FROM MemberJpaEntity m
        WHERE m.isDeleted = false
        AND (:feeStatus IS NULL OR m.feeStatus = :feeStatus)
        AND (:keyword IS NULL
            OR m.name LIKE CONCAT('%', :keyword, '%')
            OR m.studentId LIKE CONCAT('%', :keyword, '%'))
        ORDER BY m.id DESC
        """)
    Page<MemberJpaEntity> searchByFeeStatus(
        @Param("feeStatus") String feeStatus,
        @Param("keyword") String keyword,
        Pageable pageable
    );

    // 탈퇴 회원에게는 어차피 보내지 않으므로 is_deleted와 관계없이 지운다
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE MemberJpaEntity m
        SET m.fcmToken = NULL
        WHERE m.fcmToken = :fcmToken
        AND m.id <> :memberId
        """)
    void clearFcmTokenOfOthers(@Param("memberId") Long memberId, @Param("fcmToken") String fcmToken);
}
