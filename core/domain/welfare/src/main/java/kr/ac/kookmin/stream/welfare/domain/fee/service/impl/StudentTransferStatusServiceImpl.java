package kr.ac.kookmin.stream.welfare.domain.fee.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
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
    public StudentTransferStatus request(Long memberId, int remainingSemesters, String proofImageKey, long amount) {
        LocalDateTime now = LocalDateTime.now(DateUtil.KST);
        StudentTransferStatus transferStatus = studentTransferStatusRepository.findLatestByMemberId(memberId)
            .map(existing -> {
                existing.resubmit(remainingSemesters, proofImageKey, amount, now);
                return existing;
            })
            .orElseGet(() -> StudentTransferStatus.request(memberId, remainingSemesters, proofImageKey, amount, now));
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

        transferStatus.review(status, adminNote, LocalDateTime.now(DateUtil.KST));
        return studentTransferStatusRepository.save(transferStatus);
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
