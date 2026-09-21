package kr.ac.kookmin.stream.db.event;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchiveRelatedLinkJpaRepository extends JpaRepository<ArchiveRelatedLinkJpaEntity, Long> {

    /**
     * display_order에 유니크 제약이 없어 같은 값이 둘 이상일 수 있다. 식별자를 동점 기준으로 더해
     * 같은 요청이 항상 같은 순서를 돌려주게 한다.
     */
    List<ArchiveRelatedLinkJpaEntity> findAllByArchiveIdOrderByDisplayOrderAscIdAsc(Long archiveId);
}
