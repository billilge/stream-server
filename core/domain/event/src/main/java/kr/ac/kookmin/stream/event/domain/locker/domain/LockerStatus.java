package kr.ac.kookmin.stream.event.domain.locker.domain;

/** 사물함 자체의 물리 상태. 회차 배정 여부는 담지 않는다. */
public enum LockerStatus {

    AVAILABLE,
    DISABLED;

    /**
     * 학생에게 내줄 수 있는 상태인지. 쓸 수 있는 상태를 허용 목록으로 두어, 나중에 상태가 늘어도 그 값이 자동으로
     * "쓸 수 없음"에 들어가게 한다. 상태를 차단 목록으로 보는 곳이 생기면 새 값만 조용히 빠져나간다.
     */
    public boolean isUsable() {
        return this == AVAILABLE;
    }
}
