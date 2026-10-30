package kr.ac.kookmin.stream.event.domain.event.domain;

import kr.ac.kookmin.stream.common.BusinessException;

public enum EventApplicationStatus {
    APPLIED,
    CANCELED;

    /**
     * 신청 현황 필터로 들어온 문자열을 신청 상태로 바꾼다. 값이 없으면 필터를 걸지 않는다는 뜻이라 null을 돌려준다.
     * 잘못된 값에 500이 나가지 않도록 여기서 걸러 BusinessException으로 바꾼다.
     */
    public static EventApplicationStatus from(String value) {
        if (value == null) {
            return null;
        }
        try {
            return EventApplicationStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(EventErrorCode.EVENT_INVALID_APPLICATION_STATUS);
        }
    }
}
