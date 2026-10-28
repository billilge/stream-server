package kr.ac.kookmin.stream.welfare.domain.rental.domain;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CommonErrorCode;

public enum ItemType {
    CONSUMABLE,
    RENTAL;

    public static ItemType from(String value) {
        if (value == null) {
            return null;
        }
        try {
            return ItemType.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT);
        }
    }
}
