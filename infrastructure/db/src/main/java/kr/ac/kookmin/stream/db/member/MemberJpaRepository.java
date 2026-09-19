package kr.ac.kookmin.stream.db.member;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberJpaRepository extends JpaRepository<MemberJpaEntity, Long> {

    Optional<MemberJpaEntity> findByIdAndIsDeletedFalse(Long id);

    List<MemberJpaEntity> findAllByIdInAndIsDeletedFalse(List<Long> ids);

    @Query("""
        SELECT m.id FROM MemberJpaEntity m
        WHERE m.isDeleted = false
        AND (m.name LIKE CONCAT('%', :keyword, '%') OR m.studentId LIKE CONCAT('%', :keyword, '%'))
        """)
    List<Long> searchIdsByKeyword(@Param("keyword") String keyword);
}
