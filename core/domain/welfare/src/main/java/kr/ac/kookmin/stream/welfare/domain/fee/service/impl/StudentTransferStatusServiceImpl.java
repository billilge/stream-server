package kr.ac.kookmin.stream.welfare.domain.fee.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.DateUtil;
import kr.ac.kookmin.stream.common.OutboxWriter;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.common.event.MemberFeePaid;
import kr.ac.kookmin.stream.common.event.MemberFeePaymentCanceled;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.repository.StudentTransferStatusRepository;
import kr.ac.kookmin.stream.welfare.domain.fee.service.StudentTransferStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class StudentTransferStatusServiceImpl implements StudentTransferStatusService {

    private final StudentTransferStatusRepository studentTransferStatusRepository;
    private final OutboxWriter outboxWriter;

    @Override
    public Optional<StudentTransferStatus> findLatestByMemberId(Long memberId) {
        return studentTransferStatusRepository.findLatestByMemberId(memberId);
    }

    @Override
    public Map<Long, StudentTransferStatus> getMapByMemberIds(List<Long> memberIds) {
        if (memberIds.isEmpty()) {
            return Map.of();
        }
        return studentTransferStatusRepository.findLatestByMemberIds(memberIds).stream()
            .collect(Collectors.toMap(StudentTransferStatus::getMemberId, Function.identity()));
    }

    @Override
    @Transactional
    public StudentTransferStatus submit(Long memberId, int remainingSemesters, String proofImageKey, long amount) {
        // 반려된 요청 뒤에만 새 요청을 받는다. 이전 요청은 고치지 않고 이력으로 남긴다
        studentTransferStatusRepository.findLatestByMemberId(memberId)
            .ifPresent(StudentTransferStatus::validateRequestable);

        // 같은 회원의 동시 요청은 위 검사를 함께 통과할 수 있다. 그때는 진행 중 요청 유니크(V26)가 두 번째 저장을 막는다
        StudentTransferStatus transferStatus = StudentTransferStatus.create(
            memberId, remainingSemesters, proofImageKey, amount, LocalDateTime.now(DateUtil.KST));
        return studentTransferStatusRepository.save(transferStatus);
    }

    // 여기는 readOnly를 유지한다. 페이지 조회는 목록과 전체 개수를 두 번에 나눠 읽으므로,
    // 한 트랜잭션으로 묶어야 그 사이 들어온 쓰기 때문에 totalCount와 content가 어긋나지 않는다.
    @Override
    @Transactional(readOnly = true)
    public PageResult<StudentTransferStatus> search(TransferStatus status, List<Long> memberIds, PageOffset pageOffset) {
        return studentTransferStatusRepository.search(status, memberIds, pageOffset);
    }

    @Override
    @Transactional
    public StudentTransferStatus review(Long transferStatusId, TransferStatus status, String adminNote) {
        StudentTransferStatus transferStatus = studentTransferStatusRepository.findById(transferStatusId)
            .orElseThrow(() -> new BusinessException(FeeErrorCode.FEE_REQUEST_NOT_FOUND));
        requireLatest(transferStatus);

        boolean wasPaid = transferStatus.getStatus() == TransferStatus.PAID;
        LocalDateTime now = LocalDateTime.now(DateUtil.KST);
        transferStatus.review(status, adminNote, now);
        StudentTransferStatus reviewed = studentTransferStatusRepository.save(transferStatus);

        recordPaidChange(wasPaid, reviewed, now);
        return reviewed;
    }

    // 회원의 납부 여부(최신 요청이 PAID인가)가 바뀔 때만 남긴다. 최신 요청만 처리하므로 이 요청의 변화가 곧 회원의 변화다.
    // 상태 변경과 같은 트랜잭션에서 기록해서, 처리가 롤백되면 이벤트도 함께 사라진다(OutboxWriter는 트랜잭션 밖에서 부르면 실패한다).
    private void recordPaidChange(boolean wasPaid, StudentTransferStatus reviewed, LocalDateTime now) {
        boolean paid = reviewed.getStatus() == TransferStatus.PAID;
        if (!wasPaid && paid) {
            outboxWriter.record(new MemberFeePaid(reviewed.getMemberId(), reviewed.getId(), now));
        } else if (wasPaid && !paid) {
            outboxWriter.record(new MemberFeePaymentCanceled(reviewed.getMemberId(), reviewed.getId(), now));
        }
    }

    // 지난 요청은 이력이라 바꾸지 않는다. 회원의 지금 상태는 최신 요청 하나뿐이고, 지난 요청을 되살리면
    // 회원당 진행 중 요청이 둘이 될 수 있다
    private void requireLatest(StudentTransferStatus transferStatus) {
        boolean latest = studentTransferStatusRepository.findLatestByMemberId(transferStatus.getMemberId())
            .map(found -> found.getId().equals(transferStatus.getId()))
            .orElse(false);
        if (!latest) {
            throw new BusinessException(FeeErrorCode.FEE_REQUEST_NOT_LATEST);
        }
    }
}
