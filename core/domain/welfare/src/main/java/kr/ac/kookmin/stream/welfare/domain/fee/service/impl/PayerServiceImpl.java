package kr.ac.kookmin.stream.welfare.domain.fee.service.impl;

import kr.ac.kookmin.stream.welfare.domain.fee.domain.Payer;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.repository.PayerRepository;
import kr.ac.kookmin.stream.welfare.domain.fee.service.PayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class PayerServiceImpl implements PayerService {

    private final PayerRepository payerRepository;

    @Override
    @Transactional
    public void sync(Long memberId, String name, String studentId, TransferStatus status) {
        if (status == TransferStatus.PAID) {
            Payer payer = payerRepository.findByMemberId(memberId)
                .map(existing -> existing.withDetails(name, studentId, enrollmentYearOf(studentId)))
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
