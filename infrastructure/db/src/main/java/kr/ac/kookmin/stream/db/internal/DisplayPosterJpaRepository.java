package kr.ac.kookmin.stream.db.internal;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisplayPosterJpaRepository extends JpaRepository<DisplayPosterJpaEntity, Long> {

    List<DisplayPosterJpaEntity> findAllByOrderByIdDesc();
}
