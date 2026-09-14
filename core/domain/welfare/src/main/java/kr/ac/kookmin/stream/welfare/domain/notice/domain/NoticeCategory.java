package kr.ac.kookmin.stream.welfare.domain.notice.domain;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CommonErrorCode;

public enum NoticeCategory {
    GENERAL,
    PARTNERSHIP;

    public static NoticeCategory from(String value) {
        if (value == null) {
            return null;
        }
        try {
            return NoticeCategory.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT);
        }
    }
}
