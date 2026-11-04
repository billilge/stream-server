package kr.ac.kookmin.stream.welfare.domain.fee.service.impl;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.Payer;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.repository.PayerRepository;
import kr.ac.kookmin.stream.welfare.domain.fee.service.PayerService;
import kr.ac.kookmin.stream.welfare.domain.fee.service.StudentTransferStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class PayerServiceImpl implements PayerService {

    private final PayerRepository payerRepository;
    private final StudentTransferStatusService studentTransferStatusService;

    @Override
    @Transactional
    public void sync(Long memberId, String name, String studentId, TransferStatus status) {
        if (status != TransferStatus.PAID) {
            payerRepository.deleteByMemberId(memberId);
            return;
        }

        String enrollmentYear = enrollmentYearOf(studentId);
        Payer payer = payerRepository.findByMemberId(memberId).orElse(null);
        if (payer == null) {
            payer = Payer.create(memberId, name, studentId, enrollmentYear);
        } else {
            payer.updateDetails(name, studentId, enrollmentYear);
        }
        payerRepository.save(payer);
    }

    // 학번 앞 4자리를 입학년도로 본다 (레거시 빌릴게 백엔드의 PayerService와 동일한 규칙)
    private String enrollmentYearOf(String studentId) {
        return studentId.substring(0, 4);
    }

    // 조회 쿼리가 1개라 트랜잭션을 걸지 않는다(validatePayer와 같은 이유).
    @Override
    public boolean isPayer(Long memberId) {
        return payerRepository.findByMemberId(memberId).isPresent();
    }

    // 조회 쿼리가 1개라 트랜잭션을 걸지 않는다(PR#67/#69 원칙 — 조회니까 붙이는 게 아니라 실제 이득이 있을 때만 붙인다).
    // 행사 상세의 신청 자격(isPayer)과 신청 거절이 어긋나지 않도록 명부 판정은 isPayer 한 곳에 둔다.
    @Override
    public void validatePayer(Long memberId) {
        if (!isPayer(memberId)) {
            throw new BusinessException(FeeErrorCode.MEMBER_IS_NOT_PAYER);
        }
    }

    @Override
    @Transactional
    public void linkMember(Long memberId, String studentId) {
        // 이미 자기 명부 행이 있는 회원은 연결할 필요가 없다(validatePayer는 그 행으로 통과한다).
        // 건너뛰지 않으면 uk_payers_member_id 위반으로 로그인이 500이 된다
        payerRepository.findByStudentIdAndMemberIdIsNull(studentId)
            .filter(payer -> payerRepository.findByMemberId(memberId).isEmpty())
            .ifPresent(payer -> {
                payer.linkMember(memberId);
                payerRepository.save(payer);
                studentTransferStatusService.createPaidForLinkedPayer(memberId);
            });
    }
}
