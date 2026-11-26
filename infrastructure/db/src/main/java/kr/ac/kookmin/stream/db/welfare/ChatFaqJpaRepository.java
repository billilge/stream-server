package kr.ac.kookmin.stream.db.welfare;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatFaqJpaRepository extends JpaRepository<ChatFaqJpaEntity, Long> {

    Optional<ChatFaqJpaEntity> findByIdAndIsDeletedFalse(Long id);

    List<ChatFaqJpaEntity> findAllByIsDeletedFalseOrderByIdAsc();
}
