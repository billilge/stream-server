package kr.ac.kookmin.stream.welfare.domain.fee.repository;

import java.util.Optional;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.Payer;

public interface PayerRepository {
    Optional<Payer> findByMemberId(Long memberId);
    Payer save(Payer payer);
    void deleteByMemberId(Long memberId);
}
