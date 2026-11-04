package kr.ac.kookmin.stream.event.domain.locker.domain;

import kr.ac.kookmin.stream.common.BusinessException;

/**
 * 운영진 신청 현황의 신청 상태. 운영진이 취소했는지만 가른다.
 * <p>
 * 같은 도메인의 다른 두 상태와 축이 다르다 — {@link LockerApplicationStatus}는 학생의 "내 사물함"이 쓰는
 * 배정 중·사용 종료이고, {@link LockerApplyStatus}는 회차가 지금 신청을 받는지다. 운영진 목록 전용이라
 * 이름에 그대로 드러낸다.
 */
public enum AdminLockerApplicationStatus {

    APPLIED,
    CANCELED;

    /**
     * 신청 현황 필터로 들어온 문자열을 상태로 바꾼다. 값이 없으면 상태로 거르지 않는다는 뜻이라 {@code null}을
     * 돌려준다. 잘못된 값에 500이 나가지 않도록 여기서 걸러 {@link BusinessException}으로 바꾼다.
     */
    public static AdminLockerApplicationStatus from(String value) {
        if (value == null) {
            return null;
        }
        try {
            return AdminLockerApplicationStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(LockerErrorCode.LOCKER_INVALID_APPLICATION_STATUS);
        }
    }

    public boolean isCanceled() {
        return this == CANCELED;
    }
}
