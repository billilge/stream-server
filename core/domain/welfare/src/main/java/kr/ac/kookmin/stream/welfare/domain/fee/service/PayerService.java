package kr.ac.kookmin.stream.welfare.domain.fee.service;

import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.Payer;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;

public interface PayerService {

    // 확인요청 처리 결과를 납부자 명부(Payer)에 반영한다. PAID면 등록, 그 외면 명부에서 제거한다.
    void sync(Long memberId, String name, String studentId, TransferStatus status);

    // 회원이 납부자 명부에 등록돼 있는지 확인하고, 아니면 예외를 던진다(회비 납부 여부가 필요한 다른 도메인의 조건 확인용).
    void validatePayer(Long memberId);

    // 납부자 명부를 이름순으로 조회한다. keyword는 이름·학번 부분 일치이고, 비어 있으면 전체다.
    // 앱에 가입하지 않은 레거시 이관 납부자도 나오며 이 행은 memberId가 null이다
    PageResult<Payer> search(String keyword, PageOffset pageOffset);
}
