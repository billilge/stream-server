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

    /**
     * 중복을 걷어낸 사물함 식별자. 같은 칸이 여러 번 와도 결과는 같지만, 그대로 두면 이용 중 검사의 {@code IN} 절이
     * 구역의 칸 수보다 커진다. 중복을 없애면 상한이 그 구역의 칸 수가 되어 따로 숫자를 둘 필요가 없다.
     */
    public List<Long> distinctLockerIds() {
        return lockerIds.stream().distinct().toList();
    }
}
