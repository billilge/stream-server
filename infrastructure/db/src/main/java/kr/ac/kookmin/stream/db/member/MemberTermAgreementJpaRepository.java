package kr.ac.kookmin.stream.db.member;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTermAgreementJpaRepository extends JpaRepository<MemberTermAgreementJpaEntity, Long> {
    List<MemberTermAgreementJpaEntity> findAllByMemberId(Long memberId);
}
