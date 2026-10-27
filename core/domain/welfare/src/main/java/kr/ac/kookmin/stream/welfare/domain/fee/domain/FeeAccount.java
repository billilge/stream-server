package kr.ac.kookmin.stream.welfare.domain.fee.domain;

import java.util.Set;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.welfare.domain.fee.service.TossTransferLinkGenerator;

/**
 * 회비 납부 계좌와 학기당 회비. 계좌를 설정하기 전이면 bank·accountNo가 null이다.
 * <p>
 * 은행은 토스 송금 링크의 bank 파라미터와 화면 표시에 그대로 쓰이므로 enum으로 바꾸지 않고 이름 그대로 둔다.
 */
public record FeeAccount(String bank, String accountNo, long amountPerSemester) {

    // 관리자 화면의 은행 선택 목록과 같은 22개
    private static final Set<String> ALLOWED_BANKS = Set.of(
        "경남", "광주", "기업", "국민", "대구", "부산", "산림", "새마을", "SC제일", "신한", "신협",
        "수협", "케이뱅크", "우리", "우체국", "저축은행", "전북", "제주", "카카오뱅크", "토스뱅크", "하나", "농협"
    );

    /** 운영진이 새로 저장하는 계좌. 허용 목록에 있는 은행인지 검증한다. */
    public static FeeAccount create(String bank, String accountNo, long amountPerSemester) {
        validateBank(bank);
        return new FeeAccount(bank, accountNo, amountPerSemester);
    }

    // 저장된 설정값으로 복원한다. 은행 목록을 검사하지 않던 때 저장된 값이 있을 수 있어 다시 검증하지 않는다
    public static FeeAccount of(String bank, String accountNo, long amountPerSemester) {
        return new FeeAccount(bank, accountNo, amountPerSemester);
    }

    public boolean isConfigured() {
        return bank != null && accountNo != null;
    }

    /** 남은 학기 수로 계산한 금액과, 그 금액이 채워진 토스 송금 링크. */
    public TransferGuide createTransferGuide(RemainingSemesters remainingSemesters) {
        long amount = calculateTransferAmount(remainingSemesters);
        return TransferGuide.of(remainingSemesters.value(), amount,
            TossTransferLinkGenerator.generate(bank, accountNo, amount));
    }

    /** 이 학생이 보낼 금액. 남은 학기 수 × 학기당 회비. */
    public long calculateTransferAmount(RemainingSemesters remainingSemesters) {
        return remainingSemesters.calculateAmount(amountPerSemester);
    }

    /** 금액이 빠진 송금 기본 링크. 계좌가 설정되기 전이면 null이다. */
    public String generateBaseTransferLinkUrl() {
        return isConfigured() ? TossTransferLinkGenerator.generate(bank, accountNo, null) : null;
    }

    private static void validateBank(String bank) {
        if (bank == null || !ALLOWED_BANKS.contains(bank)) {
            throw new BusinessException(FeeErrorCode.INVALID_BANK);
        }
    }
}
