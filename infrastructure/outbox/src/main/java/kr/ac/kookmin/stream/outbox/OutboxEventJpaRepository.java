package kr.ac.kookmin.stream.outbox;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventJpaEntity, Long> {

    List<OutboxEventJpaEntity> findTop100ByStatusOrderByIdAsc(OutboxStatus status);

    // 파생 delete는 행을 읽어 한 건씩 지우므로 벌크 DELETE로 지운다. 호출하는 정리 작업에 트랜잭션이 없어 여기서 연다
    @Transactional
    @Modifying
    @Query("""
        DELETE FROM OutboxEventJpaEntity o
        WHERE o.status = :status
        AND o.publishedAt < :before
        """)
    int deleteByStatusAndPublishedAtBefore(@Param("status") OutboxStatus status, @Param("before") LocalDateTime before);
}
