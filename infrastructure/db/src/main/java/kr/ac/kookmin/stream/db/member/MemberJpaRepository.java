package kr.ac.kookmin.stream.db.member;

import java.util.List;
import java.util.Optional;
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
