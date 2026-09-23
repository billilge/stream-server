package kr.ac.kookmin.stream.db.event;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LockerJpaRepository extends JpaRepository<LockerJpaEntity, Long> {

    List<LockerJpaEntity> findAllByIsDeletedFalse();

    /**
     * 배치도 순서(행 → 열)로 조회한다. (row_no, column_no)에 유니크 제약이 없어 같은 자리가 둘 이상일 수 있으므로
     * 식별자를 동점 기준으로 더해 같은 요청이 항상 같은 순서를 돌려주게 한다.
     */
    List<LockerJpaEntity> findAllBySectionIdAndIsDeletedFalseOrderByRowNoAscColumnNoAscIdAsc(Long sectionId);
}
