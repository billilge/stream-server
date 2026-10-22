package kr.ac.kookmin.stream.welfare.domain.fee.service;

import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;

public interface StudentTransferStatusService {

    StudentTransferStatus getByMemberId(Long memberId);

    /**
     * 회원의 납부 상태 행을 만든다. 이미 있으면 아무것도 하지 않는다.
     * <p>
     * 모든 회원이 행을 갖게 해서 관리자 목록이 납부 전 회원까지 보여줄 수 있게 한다. 행이 없는 회원을
     * 목록에 넣으려면 회원 기준으로 조회해야 하고, 그러면 member 도메인이 회비를 알아야 한다.
     */
    void createIfAbsent(Long memberId);

    /**
     * 납부 정보(남은 학기 수·학점이수현황 캡처)를 저장한다. 상태는 납부 전 그대로다.
     * 행이 없으면 만들고, 있으면 이전 입력값을 덮어쓴다.
     */
    StudentTransferStatus submit(Long memberId, int remainingSemesters, String proofImageKey);

    /** 송금 확인을 요청한다. 납부 정보를 제출하지 않았으면 {@code FEE_REQUEST_NOT_FOUND}다. */
    StudentTransferStatus requestConfirmation(Long memberId);

    /** 회원별 납부 상태. 목록이 회원 기준이라 회원 한 페이지만큼 한 번에 가져온다. 제출 전 회원은 빠진다. */
    Map<Long, StudentTransferStatus> getMapByMemberIds(List<Long> memberIds);

    // status가 null이면 전체 상태, memberIds가 null이면 회원 필터 없음
    PageResult<StudentTransferStatus> search(TransferStatus status, List<Long> memberIds, PageOffset pageOffset);

    /** 관리자 처리. 이미 처리된 요청도 다시 바꿀 수 있다. adminNote는 확인필요일 때만 남는다. */
    StudentTransferStatus review(Long transferStatusId, TransferStatus status, String adminNote);
}
