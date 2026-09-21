package kr.ac.kookmin.stream.db.event;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventApplicationAnswerJpaRepository
    extends JpaRepository<EventApplicationAnswerJpaEntity, Long> {

    List<EventApplicationAnswerJpaEntity> findAllByEventApplicationId(Long eventApplicationId);
}
