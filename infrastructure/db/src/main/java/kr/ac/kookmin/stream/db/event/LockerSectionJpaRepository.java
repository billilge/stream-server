package kr.ac.kookmin.stream.db.event;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LockerSectionJpaRepository extends JpaRepository<LockerSectionJpaEntity, Long> {

    /**
     * 표시 순서를 따로 두지 않으므로 식별자로 정렬해 같은 요청이 항상 같은 순서를 돌려주게 한다.
     */
    List<LockerSectionJpaEntity> findAllByOrderByIdAsc();
}
