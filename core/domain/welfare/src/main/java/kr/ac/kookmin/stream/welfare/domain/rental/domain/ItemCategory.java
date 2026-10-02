package kr.ac.kookmin.stream.welfare.domain.rental.domain;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CommonErrorCode;

public enum ItemCategory {
    ELECTRONICS,
    DAILY_SUPPLIES,
    MEDICINE,
    HYGIENE;

    public static ItemCategory from(String value) {
        if (value == null) {
            return null;
        }
        try {
            return ItemCategory.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT);
        }
    }
}
