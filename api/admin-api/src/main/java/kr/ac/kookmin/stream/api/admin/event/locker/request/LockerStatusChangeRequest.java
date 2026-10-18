package kr.ac.kookmin.stream.api.admin.event.locker.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerStatus;

/**
 * 구역 안의 사물함 여러 개를 같은 상태로 바꾼다.
 * <p>
 * 구역 상세가 내려주는 {@code ASSIGNED}는 신청으로 생기는 표시용 값이라 이 경로로는 바꿀 수 없다.
 */
public record LockerStatusChangeRequest(
    @NotEmpty(message = "사물함을 하나 이상 선택해 주세요.")
    List<@NotNull(message = "사물함 ID는 비워 둘 수 없습니다.") Long> lockerIds,

    @NotNull(message = "변경할 상태를 입력해 주세요.")
    LockerStatus lockerStatus
) {
}
