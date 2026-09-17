package kr.ac.kookmin.stream.welfare.domain.fee.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.Payer;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.repository.FeeRepository;
import kr.ac.kookmin.stream.welfare.domain.fee.repository.PayerRepository;
import kr.ac.kookmin.stream.welfare.domain.fee.service.FeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class FeeServiceImpl implements FeeService {

    private final FeeRepository feeRepository;
    private final PayerRepository payerRepository;

    @Override
    @Transactional(readOnly = true)
    public StudentTransferStatus getMyFee(Long memberId) {
        return feeRepository.findByMemberId(memberId).orElseGet(() -> StudentTransferStatus.notRequested(memberId));
    }

    @Override
    @Transactional
    public StudentTransferStatus requestConfirmation(Long memberId) {
        StudentTransferStatus request = feeRepository.findByMemberId(memberId)
            .map(StudentTransferStatus::requestConfirmation)
            .orElseGet(() -> StudentTransferStatus.create(memberId));
        return feeRepository.save(request);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<StudentTransferStatus> search(String status, List<Long> memberIds, int page, int size) {
        TransferStatus transferStatus = status == null ? null : parseStatus(status, FeeErrorCode.INVALID_TRANSFER_STATUS);
        return feeRepository.search(transferStatus, memberIds, page, size);
    }

    @Override
    @Transactional
    public StudentTransferStatus review(Long feeId, String status) {
        TransferStatus reviewedStatus = parseStatus(status, FeeErrorCode.INVALID_FEE_STATUS);
        if (reviewedStatus == TransferStatus.PENDING) {
            throw new BusinessException(FeeErrorCode.INVALID_FEE_STATUS);
        }

        StudentTransferStatus request = feeRepository.findById(feeId)
            .orElseThrow(() -> new BusinessException(FeeErrorCode.FEE_REQUEST_NOT_FOUND));
        if (request.getStatus() != TransferStatus.PENDING) {
            throw new BusinessException(FeeErrorCode.FEE_REQUEST_ALREADY_REVIEWED);
        }

        return feeRepository.save(request.review(reviewedStatus, LocalDateTime.now()));
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

    // Spring MVC는 잘못된 enum 문자열을 MethodArgumentNotValidException이 아닌 다른 예외로 던지므로
    // 원하는 커스텀 에러 코드를 응답하려면 String으로 받아 여기서 직접 파싱한다.
    private TransferStatus parseStatus(String status, FeeErrorCode invalidCode) {
        try {
            return TransferStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(invalidCode);
        }
    }
}
