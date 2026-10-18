package kr.ac.kookmin.stream.outbox;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.common.DateUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 발행을 마친 아웃박스 행을 정리한다. 실패(FAILED) 행은 원인을 확인할 수 있게 남겨 둔다.
 */
@Component
@RequiredArgsConstructor
public class OutboxCleaner {

    private static final Logger log = LoggerFactory.getLogger(OutboxCleaner.class);

    private static final long RETENTION_DAYS = 7;

    private final OutboxEventJpaRepository outboxEventJpaRepository;

    // 발행이 몰리지 않는 새벽에 돈다
    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
    public void deletePublished() {
        LocalDateTime before = LocalDateTime.now(DateUtil.KST).minusDays(RETENTION_DAYS);
        int deleted = outboxEventJpaRepository.deleteByStatusAndPublishedAtBefore(OutboxStatus.PUBLISHED, before);
        log.info("발행 완료 아웃박스 정리: deleted={}, before={}", deleted, before);
    }
}
