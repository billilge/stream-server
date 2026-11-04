package kr.ac.kookmin.stream.db.member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.Role;
import kr.ac.kookmin.stream.member.domain.member.domain.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface MemberJpaRepository extends JpaRepository<MemberJpaEntity, Long> {

    Optional<MemberJpaEntity> findByIdAndIsDeletedFalse(Long id);

    Optional<MemberJpaEntity> findByStudentIdAndIsDeletedFalse(String studentId);

    boolean existsByPhoneNumberAndIsDeletedFalse(String phoneNumber);

    boolean existsByEmailAndIsDeletedFalse(String email);

    List<MemberJpaEntity> findAllByIdInAndIsDeletedFalse(List<Long> ids);

    // keyword는 LikePatterns.escape로 이스케이프한 검색어다. 쿼리의 ESCAPE 문자는 LikePatterns.ESCAPE와 같아야 한다
    @Query("""
        SELECT m.id FROM MemberJpaEntity m
        WHERE m.isDeleted = false
        AND (m.name LIKE CONCAT('%', :keyword, '%') ESCAPE '!'
            OR m.studentId LIKE CONCAT('%', :keyword, '%') ESCAPE '!')
        """)
    List<Long> searchIdsByKeyword(@Param("keyword") String keyword);

    @Query("SELECT m.id FROM MemberJpaEntity m WHERE m.isDeleted = false")
    List<Long> findAllActiveIds();

    @Query("""
        SELECT m FROM MemberJpaEntity m
        WHERE m.isDeleted = false
        AND m.role = :role
        AND m.councilDepartment IS NOT NULL
        ORDER BY m.name, m.studentId
        """)
    List<MemberJpaEntity> findAllCouncilMembers(@Param("role") Role role);

    // 운영진 콘솔 회원 목록. keyword는 LikePatterns.escape로 이스케이프한 검색어다.
    // 쿼리의 ESCAPE 문자는 LikePatterns.ESCAPE와 같아야 한다
    @Query(value = """
        SELECT m FROM MemberJpaEntity m
        WHERE m.isDeleted = false
        AND (:keyword IS NULL
            OR m.name LIKE CONCAT('%', :keyword, '%') ESCAPE '!'
            OR m.studentId LIKE CONCAT('%', :keyword, '%') ESCAPE '!'
            OR m.email LIKE CONCAT('%', :keyword, '%') ESCAPE '!')
        AND (:department IS NULL OR m.department = :department)
        AND (:role IS NULL OR m.role = :role)
        ORDER BY m.name ASC, m.id ASC
        """,
        countQuery = """
        SELECT COUNT(m) FROM MemberJpaEntity m
        WHERE m.isDeleted = false
        AND (:keyword IS NULL
            OR m.name LIKE CONCAT('%', :keyword, '%') ESCAPE '!'
            OR m.studentId LIKE CONCAT('%', :keyword, '%') ESCAPE '!'
            OR m.email LIKE CONCAT('%', :keyword, '%') ESCAPE '!')
        AND (:department IS NULL OR m.department = :department)
        AND (:role IS NULL OR m.role = :role)
        """)
    Page<MemberJpaEntity> search(
        @Param("keyword") String keyword,
        @Param("department") Department department,
        @Param("role") Role role,
        Pageable pageable
    );

    // 알림 설정이 없는 회원(로그인만 하고 가입을 마치지 않은 회원)과, 알림 수신 동의(NOTIFICATION_CONSENT) 약관에
    // 동의하지 않은 회원은 푸시 대상에서 빠진다. 카테고리별 설정은 그 동의 안에서의 세부 선택이다
    @Query("""
        SELECT new kr.ac.kookmin.stream.db.member.PushTargetProjection(m.id, m.fcmToken, s)
        FROM MemberJpaEntity m
        JOIN MemberNotificationSettingJpaEntity s ON s.memberId = m.id
        JOIN MemberTermAgreementJpaEntity t ON t.memberId = m.id
            AND t.termType = kr.ac.kookmin.stream.member.domain.member.domain.TermType.NOTIFICATION_CONSENT
            AND t.agreed = true
        WHERE m.isDeleted = false
        AND m.fcmToken IS NOT NULL
        AND m.id IN :memberIds
        """)
    List<PushTargetProjection> findPushTargetsByIdIn(@Param("memberIds") Collection<Long> memberIds);

    @Query("""
        SELECT new kr.ac.kookmin.stream.db.member.PushTargetProjection(m.id, m.fcmToken, s)
        FROM MemberJpaEntity m
        JOIN MemberNotificationSettingJpaEntity s ON s.memberId = m.id
        JOIN MemberTermAgreementJpaEntity t ON t.memberId = m.id
            AND t.termType = kr.ac.kookmin.stream.member.domain.member.domain.TermType.NOTIFICATION_CONSENT
            AND t.agreed = true
        WHERE m.isDeleted = false
        AND m.fcmToken IS NOT NULL
        """)
    List<PushTargetProjection> findAllPushTargets();

    // 탈퇴 회원에게는 어차피 보내지 않으므로 is_deleted와 관계없이 지운다
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE MemberJpaEntity m
        SET m.fcmToken = NULL
        WHERE m.fcmToken = :fcmToken
        AND m.id <> :memberId
        """)
    void clearFcmTokenOfOthers(@Param("memberId") Long memberId, @Param("fcmToken") String fcmToken);

    // 발송 이후 새로 등록된 토큰까지 지우지 않도록 회원 ID가 아니라 FCM이 무효라고 한 토큰 값으로 지운다.
    // 알림 발송 처리는 트랜잭션 밖에서 돌기 때문에 여기서 연다
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE MemberJpaEntity m
        SET m.fcmToken = NULL
        WHERE m.fcmToken IN :fcmTokens
        """)
    int clearFcmTokens(@Param("fcmTokens") Collection<String> fcmTokens);
}
