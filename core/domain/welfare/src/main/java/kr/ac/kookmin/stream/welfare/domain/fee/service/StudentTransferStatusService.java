package kr.ac.kookmin.stream.welfare.domain.fee.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;

public interface StudentTransferStatusService {

    /** 회원의 최신 납부 요청 — 곧 회원의 지금 상태다. 한 번도 요청하지 않은 회원(미납부)이면 비어 있다. */
    Optional<StudentTransferStatus> findLatestByMemberId(Long memberId);

    /**
     * 학기 인증과 송금 완료를 한 번에 받아 납부확인중인 새 요청을 만든다. 처음이든 반려 뒤든 늘 새 행이고,
     * 이전 요청은 그대로 남는다. 최신 요청이 납부확인중·납부완료면 {@code FEE_REQUEST_ALREADY_REQUESTED}다.
     *
     * @param amount 요청 시점에 안내한 납부 금액
     */
    StudentTransferStatus request(Long memberId, int remainingSemesters, String proofImageKey, long amount);

    /** 회원별 최신 납부 요청. 목록이 회원 기준이라 회원 한 페이지만큼 한 번에 가져온다. 한 번도 요청하지 않은 회원은 빠진다. */
    Map<Long, StudentTransferStatus> getMapByMemberIds(List<Long> memberIds);

    // status가 null이면 전체 상태, memberIds가 null이면 회원 필터 없음
    PageResult<StudentTransferStatus> search(TransferStatus status, List<Long> memberIds, PageOffset pageOffset);

    /**
     * 관리자 처리. 이미 처리된 요청도 다시 바꿀 수 있다. adminNote는 확인필요일 때만 남고,
     * 확인필요인데 비어 있으면 {@code INVALID_ADMIN_NOTE}다.
     * 회원의 최신 요청만 바꿀 수 있고, 지난 요청이면 {@code FEE_REQUEST_NOT_LATEST}다.
     * 납부 여부가 바뀌면(납부완료가 되거나 납부완료에서 벗어나면) 같은 트랜잭션에서 아웃박스에
     * {@code MemberFeePaid} · {@code MemberFeePaymentCanceled}를 남긴다.
     */
    StudentTransferStatus review(Long transferStatusId, TransferStatus status, String adminNote);
}
