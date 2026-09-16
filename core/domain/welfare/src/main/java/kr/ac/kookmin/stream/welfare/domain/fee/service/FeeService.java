package kr.ac.kookmin.stream.welfare.domain.fee.service;

import java.util.List;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentFee;

public interface FeeService {

    StudentFee getMyFee(Long memberId);

    StudentFee requestConfirmation(Long memberId);

    // status가 null이면 전체 상태, memberIds가 null이면 회원 필터 없음
    PageResult<StudentFee> search(String status, List<Long> memberIds, int page, int size);

    StudentFee review(Long feeId, String status);
}
