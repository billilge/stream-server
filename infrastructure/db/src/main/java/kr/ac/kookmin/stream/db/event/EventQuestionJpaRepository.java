package kr.ac.kookmin.stream.db.event;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventQuestionJpaRepository extends JpaRepository<EventQuestionJpaEntity, Long> {

    List<EventQuestionJpaEntity> findAllByEventIdOrderByDisplayOrderAsc(Long eventId);

    @Modifying
    @Query("DELETE FROM EventQuestionJpaEntity q WHERE q.eventId = :eventId")
    void deleteAllByEventId(@Param("eventId") Long eventId);
}
