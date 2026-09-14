package kr.ac.kookmin.stream.db.event;

import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventApplicationJpaRepository extends JpaRepository<EventApplicationJpaEntity, Long> {

    long countByEventIdAndStatus(Long eventId, EventApplicationStatus status);

    boolean existsByEventIdAndMemberIdAndStatus(Long eventId, Long memberId, EventApplicationStatus status);
}
