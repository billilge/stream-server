package kr.ac.kookmin.stream.welfare.domain.fee.service;

import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;

public interface PayerService {

    // 확인요청 처리 결과를 납부자 명부(Payer)에 반영한다. PAID면 등록, 그 외(UNPAID)면 명부에서 제거한다.
    void sync(Long memberId, String name, String studentId, TransferStatus status);
}
