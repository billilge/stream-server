package kr.ac.kookmin.stream.welfare.domain.fee.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.DateUtil;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
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

    // 조회 쿼리가 1개라 트랜잭션을 걸지 않는다(PR#67/#69 원칙 — 조회니까 붙이는 게 아니라 실제 이득이 있을 때만 붙인다).
    @Override
    public StudentTransferStatus getByMemberId(Long memberId) {
        return studentTransferStatusRepository.findByMemberId(memberId)
            .orElseGet(() -> StudentTransferStatus.notRequested(memberId));
    }

    @Override
    @Transactional
    public StudentTransferStatus submit(Long memberId, int remainingSemesters, String proofImageKey) {
        StudentTransferStatus transferStatus = studentTransferStatusRepository.findByMemberId(memberId)
            .orElseGet(() -> StudentTransferStatus.create(memberId));
        transferStatus.submit(remainingSemesters, proofImageKey);
        return studentTransferStatusRepository.save(transferStatus);
    }

    // 행이 있어도 남은 학기 수가 없으면 제출 전이다(옛 흐름에서 만들어진 행이 여기 해당한다).
    @Override
    @Transactional
    public StudentTransferStatus requestConfirmation(Long memberId) {
        StudentTransferStatus transferStatus = studentTransferStatusRepository.findByMemberId(memberId)
            .filter(existing -> existing.getRemainingSemesters() != null)
            .orElseThrow(() -> new BusinessException(FeeErrorCode.FEE_REQUEST_NOT_FOUND));
        transferStatus.requestConfirmation(LocalDateTime.now(DateUtil.KST));
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
    public StudentTransferStatus review(Long transferStatusId, TransferStatus status) {
        if (status == TransferStatus.PENDING) {
            throw new BusinessException(FeeErrorCode.INVALID_TRANSFER_STATUS);
        }

        StudentTransferStatus transferStatus = studentTransferStatusRepository.findById(transferStatusId)
            .orElseThrow(() -> new BusinessException(FeeErrorCode.FEE_REQUEST_NOT_FOUND));
        if (transferStatus.getStatus() != TransferStatus.PENDING) {
            throw new BusinessException(FeeErrorCode.FEE_REQUEST_ALREADY_REVIEWED);
        }

        transferStatus.review(status, LocalDateTime.now());
        return studentTransferStatusRepository.save(transferStatus);
    }
}
