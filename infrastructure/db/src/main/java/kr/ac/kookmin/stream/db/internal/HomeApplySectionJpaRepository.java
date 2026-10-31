package kr.ac.kookmin.stream.db.internal;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeApplySectionJpaRepository extends JpaRepository<HomeApplySectionJpaEntity, Long> {

    List<HomeApplySectionJpaEntity> findAllByIsExposedTrueOrderByDisplayOrderAscIdAsc();
}
