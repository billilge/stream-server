package kr.ac.kookmin.stream.db.welfare;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayerJpaRepository extends JpaRepository<PayerJpaEntity, Long> {
    Optional<PayerJpaEntity> findByMemberId(Long memberId);
    // student_id에는 유니크가 없어 같은 학번의 미연결 행이 둘 이상일 수 있다. 단건 Optional이 깨지지 않게 하나만 받는다
    Optional<PayerJpaEntity> findFirstByStudentIdAndMemberIdIsNullOrderByIdAsc(String studentId);
    void deleteByMemberId(Long memberId);
}
