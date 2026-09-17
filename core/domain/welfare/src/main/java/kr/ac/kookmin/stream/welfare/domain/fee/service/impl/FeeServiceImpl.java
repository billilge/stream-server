package kr.ac.kookmin.stream.welfare.domain.fee.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentFee;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.repository.FeeRepository;
import kr.ac.kookmin.stream.welfare.domain.fee.service.FeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class FeeServiceImpl implements FeeService {

    private final FeeRepository feeRepository;

    @Override
    @Transactional(readOnly = true)
    public StudentFee getMyFee(Long memberId) {
        return feeRepository.findByMemberId(memberId).orElseGet(() -> StudentFee.notRequested(memberId));
    }

    @Override
    @Transactional
    public StudentFee requestConfirmation(Long memberId) {
        StudentFee fee = feeRepository.findByMemberId(memberId)
            .map(StudentFee::requestConfirmation)
            .orElseGet(() -> StudentFee.create(memberId));
        return feeRepository.save(fee);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<StudentFee> search(String status, List<Long> memberIds, int page, int size) {
        TransferStatus transferStatus = status == null ? null : parseStatus(status, FeeErrorCode.INVALID_TRANSFER_STATUS);
        return feeRepository.search(transferStatus, memberIds, page, size);
    }

    @Override
    @Transactional
    public StudentFee review(Long feeId, String status) {
        TransferStatus reviewedStatus = parseStatus(status, FeeErrorCode.INVALID_FEE_STATUS);
        if (reviewedStatus == TransferStatus.PENDING) {
            throw new BusinessException(FeeErrorCode.INVALID_FEE_STATUS);
        }

        StudentFee fee = feeRepository.findById(feeId)
            .orElseThrow(() -> new BusinessException(FeeErrorCode.FEE_REQUEST_NOT_FOUND));
        if (fee.getStatus() != TransferStatus.PENDING) {
            throw new BusinessException(FeeErrorCode.FEE_REQUEST_ALREADY_REVIEWED);
        }

        return feeRepository.save(fee.review(reviewedStatus, LocalDateTime.now()));
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
