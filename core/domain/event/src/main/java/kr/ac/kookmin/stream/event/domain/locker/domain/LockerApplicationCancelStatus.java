package kr.ac.kookmin.stream.event.domain.locker.domain;

import kr.ac.kookmin.stream.common.BusinessException;

/**
 * 신청의 취소 여부. 지금은 운영진 신청 현황의 필터로 쓴다.
 * <p>
 * 같은 도메인의 다른 두 상태와 축이 다르다 — {@link LockerApplicationStatus}는 학생의 "내 사물함"이 쓰는
 * 배정 중·사용 종료이고, {@link LockerApplyStatus}는 회차가 지금 신청을 받는지다. 이 enum은 취소 여부만
 * 가르므로 이름에 그 축을 드러낸다.
 */
public enum LockerApplicationCancelStatus {

    APPLIED,
    CANCELED;

    /**
     * 신청 현황 필터로 들어온 문자열을 상태로 바꾼다. 값이 없으면 상태로 거르지 않는다는 뜻이라 {@code null}을
     * 돌려준다. 잘못된 값에 500이 나가지 않도록 여기서 걸러 {@link BusinessException}으로 바꾼다.
     */
    public static LockerApplicationCancelStatus from(String value) {
        if (value == null) {
            return null;
        }
        try {
            return LockerApplicationCancelStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(LockerErrorCode.LOCKER_INVALID_APPLICATION_STATUS);
        }
    }

    public boolean isCanceled() {
        return this == CANCELED;
    }
}
