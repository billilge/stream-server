package kr.ac.kookmin.stream.db.member;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<MemberJpaEntity, Long> {
    Optional<MemberJpaEntity> findByIdAndDeletedAtIsNull(Long id);
}
