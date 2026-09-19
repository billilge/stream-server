package kr.ac.kookmin.stream.db.welfare;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayerJpaRepository extends JpaRepository<PayerJpaEntity, Long> {
    Optional<PayerJpaEntity> findByMemberId(Long memberId);
    void deleteByMemberId(Long memberId);
}
