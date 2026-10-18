package kr.ac.kookmin.stream.api.admin.event.locker.response;

import java.util.List;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerStatus;

/**
 * 요청한 사물함 전체를 바뀐 뒤 상태로 돌려준다. 이미 그 상태였던 사물함도 담긴다.
 */
public record AdminLockerStatusChangeResponse(List<LockerItem> lockers) {

    public static AdminLockerStatusChangeResponse from(List<Locker> lockers) {
        return new AdminLockerStatusChangeResponse(lockers.stream()
            .map(LockerItem::from)
            .toList());
    }

    /** 도메인 {@link Locker}와 이름이 겹치지 않도록 {@code LockerItem}으로 둔다. */
    public record LockerItem(Long lockerId, int lockerNumber, LockerStatus lockerStatus) {

        public static LockerItem from(Locker locker) {
            return new LockerItem(locker.getId(), locker.getLockerNumber(), locker.getStatus());
        }
    }
}
