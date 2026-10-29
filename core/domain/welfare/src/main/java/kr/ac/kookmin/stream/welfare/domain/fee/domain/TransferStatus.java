package kr.ac.kookmin.stream.welfare.domain.fee.domain;

import kr.ac.kookmin.stream.common.BusinessException;

// 납부 요청의 상태. 한 번도 요청하지 않은 회원(미납부)은 행이 없으므로 여기에 상태가 없다.
// 진행 중·완료(PENDING, PAID)는 V22의 active_member_id 식에 그대로 적혀 있어, 상태를 더하거나 바꾸면 그 식도 같이 바꿔야 한다
public enum TransferStatus {
    PENDING,
    PAID,
    // 운영진이 납부를 확인하지 못해 돌려보낸 상태. 화면에서는 "확인필요"로 보인다
    REJECTED;

    // Spring MVC는 잘못된 enum 문자열을 MethodArgumentNotValidException이 아닌 다른 예외로 던지므로
    // 컨트롤러가 String으로 받아 이 메서드로 변환한다. null(상태 필터 미지정)은 그대로 null을 돌려준다.
    public static TransferStatus from(String value) {
        if (value == null) {
            return null;
        }
        try {
            return TransferStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(FeeErrorCode.INVALID_TRANSFER_STATUS);
        }
    }
}
