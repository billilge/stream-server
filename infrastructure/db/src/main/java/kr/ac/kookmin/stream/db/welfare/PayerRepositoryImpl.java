package kr.ac.kookmin.stream.db.welfare;

import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.db.common.LikePatterns;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.Payer;
import kr.ac.kookmin.stream.welfare.domain.fee.repository.PayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PayerRepositoryImpl implements PayerRepository {

    private final PayerJpaRepository payerJpaRepository;

    @Override
    public Optional<Payer> findByMemberId(Long memberId) {
        return payerJpaRepository.findByMemberId(memberId).map(PayerJpaEntity::toDomain);
    }

    @Override
    public PageResult<Payer> search(String keyword, PageOffset pageOffset) {
        Page<PayerJpaEntity> result = payerJpaRepository.search(
            LikePatterns.escape(keyword), PageRequest.of(pageOffset.page(), pageOffset.size())
        );

        return PageResult.of(
            result.getContent().stream().map(PayerJpaEntity::toDomain).toList(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements()
        );
    }

    @Override
    public Optional<Payer> findByStudentIdAndMemberIdIsNull(String studentId) {
        return payerJpaRepository.findFirstByStudentIdAndMemberIdIsNullOrderByIdAsc(studentId).map(PayerJpaEntity::toDomain);
    }

    @Override
    public Payer save(Payer payer) {
        return payerJpaRepository.save(PayerJpaEntity.from(payer)).toDomain();
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        payerJpaRepository.deleteByMemberId(memberId);
    }
}
