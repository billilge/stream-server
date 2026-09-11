package kr.ac.kookmin.stream.db.event;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventQuestionJpaRepository extends JpaRepository<EventQuestionJpaEntity, Long> {

    List<EventQuestionJpaEntity> findAllByEventIdOrderByDisplayOrderAsc(Long eventId);
}
