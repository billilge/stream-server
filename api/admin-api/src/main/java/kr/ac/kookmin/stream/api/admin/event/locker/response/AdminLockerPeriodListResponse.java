package kr.ac.kookmin.stream.api.admin.event.locker.response;

import java.util.List;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriodStatus;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriodSummary;

/**
 * @param currentPeriod 진행 중인 회차. 없으면 {@code null}. 회차 기간은 서로 겹치지 않으므로 최대 한 건이다
 * @param periods       전체 회차. 진행 중인 회차도 함께 들어 있다
 */
public record AdminLockerPeriodListResponse(
    AdminLockerPeriodListItemResponse currentPeriod,
    List<AdminLockerPeriodListItemResponse> periods
) {

    public static AdminLockerPeriodListResponse from(List<LockerPeriodSummary> summaries) {
        return new AdminLockerPeriodListResponse(
            summaries.stream()
                .filter(summary -> summary.status() == LockerPeriodStatus.IN_PROGRESS)
                .findFirst()
                .map(AdminLockerPeriodListItemResponse::from)
                .orElse(null),
            summaries.stream()
                .map(AdminLockerPeriodListItemResponse::from)
                .toList()
        );
    }
}
