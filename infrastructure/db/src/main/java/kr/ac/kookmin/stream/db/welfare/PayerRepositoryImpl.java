package kr.ac.kookmin.stream.db.welfare;

import java.util.Optional;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.Payer;
import kr.ac.kookmin.stream.welfare.domain.fee.repository.PayerRepository;
import lombok.RequiredArgsConstructor;
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
    public Payer save(Payer payer) {
        return payerJpaRepository.save(PayerJpaEntity.from(payer)).toDomain();
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        payerJpaRepository.deleteByMemberId(memberId);
    }
}
