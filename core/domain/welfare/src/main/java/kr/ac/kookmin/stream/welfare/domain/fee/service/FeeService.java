package kr.ac.kookmin.stream.welfare.domain.fee.service;

import java.util.List;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferRequest;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;

public interface FeeService {

    StudentTransferRequest getMyFee(Long memberId);

    StudentTransferRequest requestConfirmation(Long memberId);

    // status가 null이면 전체 상태, memberIds가 null이면 회원 필터 없음
    PageResult<StudentTransferRequest> search(String status, List<Long> memberIds, int page, int size);

    StudentTransferRequest review(Long feeId, String status);

    // review() 직후 회원 정보(Member)를 아는 호출부(UseCase)가 이어서 호출해 납부자 명부(Payer)를 맞춘다.
    // PAID면 등록/갱신, 그 외(UNPAID)면 명부에서 제거한다.
    void syncPayer(Long memberId, String name, String studentId, TransferStatus status);
}
