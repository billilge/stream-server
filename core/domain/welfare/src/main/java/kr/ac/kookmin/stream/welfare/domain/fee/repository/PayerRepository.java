package kr.ac.kookmin.stream.welfare.domain.fee.repository;

import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.Payer;

public interface PayerRepository {
    Optional<Payer> findByMemberId(Long memberId);

    // keyword는 이름·학번 부분 일치이고, null이거나 비어 있으면 전체다. 이름순으로 정렬한다
    PageResult<Payer> search(String keyword, PageOffset pageOffset);

    Payer save(Payer payer);
    void deleteByMemberId(Long memberId);
}
