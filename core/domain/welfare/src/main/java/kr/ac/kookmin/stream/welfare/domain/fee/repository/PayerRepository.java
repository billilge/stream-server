package kr.ac.kookmin.stream.welfare.domain.fee.repository;

import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.Payer;

public interface PayerRepository {
    Optional<Payer> findByMemberId(Long memberId);

    // keyword는 이름·학번 부분 일치이고, null이거나 비어 있으면 전체다. 이름순으로 정렬한다
    PageResult<Payer> search(String keyword, PageOffset pageOffset);

    /** 연결된 회원이 없는(member_id가 null인) 명부 행을 학번으로 찾는다. 같은 학번의 행이 여러 개면 가장 먼저 생긴 행 하나를 돌려준다. */
    Optional<Payer> findByStudentIdAndMemberIdIsNull(String studentId);

    Payer save(Payer payer);
    void deleteByMemberId(Long memberId);
}
