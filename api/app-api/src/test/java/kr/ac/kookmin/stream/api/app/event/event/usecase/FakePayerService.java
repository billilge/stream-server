package kr.ac.kookmin.stream.api.app.event.event.usecase;

import java.util.HashSet;
import java.util.Set;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.service.PayerService;

/**
 * 행사 UseCase 테스트용 납부자 명부. 명부 확인만 흉내 내고, 이 테스트들이 쓰지 않는 명부 반영은 막아 둔다.
 */
class FakePayerService implements PayerService {

    final Set<Long> payerIds = new HashSet<>();

    @Override
    public void sync(Long memberId, String name, String studentId, TransferStatus status) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPayer(Long memberId) {
        return payerIds.contains(memberId);
    }

    @Override
    public void validatePayer(Long memberId) {
        if (!payerIds.contains(memberId)) {
            throw new BusinessException(FeeErrorCode.MEMBER_IS_NOT_PAYER);
        }
    }
}
