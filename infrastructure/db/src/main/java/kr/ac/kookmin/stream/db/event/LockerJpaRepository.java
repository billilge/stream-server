package kr.ac.kookmin.stream.db.event;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LockerJpaRepository extends JpaRepository<LockerJpaEntity, Long> {

    List<LockerJpaEntity> findAllByIsDeletedFalse();

    Optional<LockerJpaEntity> findByIdAndIsDeletedFalse(Long id);

    /**
     * 삭제 여부와 무관하게 조회한다. 상속한 {@code findAllById}는 자체 읽기 전용 트랜잭션을 열어
     * 트랜잭션 없이 호출해도 트랜잭션 관리 문장이 붙으므로 선언 쿼리로 둔다.
     */
    List<LockerJpaEntity> findAllByIdIn(Collection<Long> ids);

    /**
     * 배치도 순서(행 → 열)로 조회한다. (row_no, column_no)에 유니크 제약이 없어 같은 자리가 둘 이상일 수 있으므로
     * 식별자를 동점 기준으로 더해 같은 요청이 항상 같은 순서를 돌려주게 한다.
     */
    List<LockerJpaEntity> findAllBySectionIdAndIsDeletedFalseOrderByRowNoAscColumnNoAscIdAsc(Long sectionId);
}
