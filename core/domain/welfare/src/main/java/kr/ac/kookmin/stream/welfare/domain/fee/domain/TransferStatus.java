package kr.ac.kookmin.stream.welfare.domain.fee.domain;

import kr.ac.kookmin.stream.common.BusinessException;

public enum TransferStatus {
    UNPAID,
    PENDING,
    PAID;

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
