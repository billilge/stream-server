package kr.ac.kookmin.stream.welfare.domain.fee.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.Payer;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.repository.PayerRepository;
import kr.ac.kookmin.stream.welfare.domain.fee.repository.StudentTransferStatusRepository;
import kr.ac.kookmin.stream.welfare.domain.fee.service.StudentTransferStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class StudentTransferStatusServiceImpl implements StudentTransferStatusService {

    private final StudentTransferStatusRepository studentTransferStatusRepository;
    private final PayerRepository payerRepository;

    @Override
    @Transactional(readOnly = true)
    public StudentTransferStatus getByMemberId(Long memberId) {
        return studentTransferStatusRepository.findByMemberId(memberId)
            .orElseGet(() -> StudentTransferStatus.notRequested(memberId));
    }

    @Override
    @Transactional
    public StudentTransferStatus requestConfirmation(Long memberId) {
        StudentTransferStatus transferStatus = studentTransferStatusRepository.findByMemberId(memberId)
            .map(StudentTransferStatus::requestConfirmation)
            .orElseGet(() -> StudentTransferStatus.create(memberId));
        return studentTransferStatusRepository.save(transferStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<StudentTransferStatus> search(TransferStatus status, List<Long> memberIds, int page, int size) {
        return studentTransferStatusRepository.search(status, memberIds, page, size);
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

        return studentTransferStatusRepository.save(transferStatus.review(status, LocalDateTime.now()));
    }

    @Override
    @Transactional
    public void syncPayer(Long memberId, String name, String studentId, TransferStatus status) {
        if (status == TransferStatus.PAID) {
            Payer payer = payerRepository.findByMemberId(memberId)
                .orElseGet(() -> Payer.create(memberId, name, studentId, enrollmentYearOf(studentId)));
            payerRepository.save(payer);
        } else {
            payerRepository.deleteByMemberId(memberId);
        }
    }

    // 학번 앞 4자리를 입학년도로 본다 (레거시 빌릴게 백엔드의 PayerService와 동일한 규칙)
    private String enrollmentYearOf(String studentId) {
        return studentId.substring(0, 4);
    }
}
