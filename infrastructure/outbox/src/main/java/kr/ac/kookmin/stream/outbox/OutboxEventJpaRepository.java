package kr.ac.kookmin.stream.outbox;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventJpaEntity, Long> {

    List<OutboxEventJpaEntity> findTop100ByStatusOrderByIdAsc(OutboxStatus status);
}
