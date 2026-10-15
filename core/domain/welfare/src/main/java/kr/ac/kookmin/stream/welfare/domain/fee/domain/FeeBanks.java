package kr.ac.kookmin.stream.welfare.domain.fee.domain;

import java.util.Set;
import kr.ac.kookmin.stream.common.BusinessException;

/**
 * 회비 납부 계좌로 쓸 수 있는 은행. 관리자 화면의 은행 선택 목록과 같은 22개다.
 * <p>
 * enum이 아니라 문자열 집합인 이유는, 이 값이 토스 송금 링크의 {@code bank} 파라미터로 그대로 나가고
 * 화면에도 그대로 표시되기 때문이다. 중간에 코드로 바꿨다가 되돌리면 한쪽만 고쳤을 때 조용히 어긋난다.
 */
public final class FeeBanks {

    private static final Set<String> ALLOWED = Set.of(
        "경남", "광주", "기업", "국민", "대구", "부산", "산림", "새마을", "SC제일", "신한", "신협",
        "수협", "케이뱅크", "우리", "우체국", "저축은행", "전북", "제주", "카카오뱅크", "토스뱅크", "하나", "농협"
    );

    private FeeBanks() {}

    public static void validate(String bank) {
        if (bank == null || !ALLOWED.contains(bank)) {
            throw new BusinessException(FeeErrorCode.INVALID_BANK);
        }
    }
}
