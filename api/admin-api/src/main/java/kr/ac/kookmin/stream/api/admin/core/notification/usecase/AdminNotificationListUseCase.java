package kr.ac.kookmin.stream.api.admin.core.notification.usecase;

import java.util.List;
import java.util.Map;
import java.util.Set;
import kr.ac.kookmin.stream.api.admin.core.notification.response.AdminNotificationListItemResponse;
import kr.ac.kookmin.stream.api.admin.core.notification.response.AdminNotificationListItemResponse.RentalSummary;
import kr.ac.kookmin.stream.api.common.dto.CursorSliceResponse;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.member.domain.notification.domain.Notification;
import kr.ac.kookmin.stream.member.domain.notification.domain.NotificationCursor;
import kr.ac.kookmin.stream.member.domain.notification.domain.NotificationType;
import kr.ac.kookmin.stream.member.domain.notification.service.AdminNotificationService;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;
import kr.ac.kookmin.stream.welfare.domain.rental.service.AdminRentalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 운영진 공용 알림함 목록. {@code notification}(알림), {@code rental}(대여 기록·물품), {@code member}(신청자 이름)를 조합한다.
 * <p>
 * 알림 문구는 만들어질 때 고정되므로, 대여 알림에는 가리키는 대여 기록의 지금 값을 요약해 실어 화면이 대여 건을 그리고
 * 버튼을 고르게 한다.
 */
@Component
@RequiredArgsConstructor
public class AdminNotificationListUseCase {

    // targetId가 대여 기록 ID인 운영진 알림
    private static final Set<NotificationType> RENTAL_TARGET_TYPES = Set.of(
        NotificationType.ADMIN_RENTAL_APPLY,
        NotificationType.ADMIN_RENTAL_CANCEL,
        NotificationType.ADMIN_RETURN_APPLY
    );

    private final AdminNotificationService adminNotificationService;
    private final AdminRentalHistoryService adminRentalHistoryService;
    private final MemberService memberService;

    public CursorSliceResponse<AdminNotificationListItemResponse> getNotifications(NotificationCursor cursor, int size) {
        CursorSliceResult<Notification> result = adminNotificationService.getNotifications(cursor, size);

        List<Long> rentalHistoryIds = result.content().stream()
            .filter(AdminNotificationListUseCase::targetsRentalHistory)
            .map(Notification::getTargetId)
            .distinct()
            .toList();
        Map<Long, RentalRecord> recordByHistoryId = rentalHistoryIds.isEmpty()
            ? Map.of()
            : adminRentalHistoryService.getRecordMapByIds(rentalHistoryIds);
        List<Long> memberIds = recordByHistoryId.values().stream()
            .map(record -> record.history().getMemberId())
            .distinct()
            .toList();
        Map<Long, Member> memberById = memberIds.isEmpty() ? Map.of() : memberService.getMapByIds(memberIds);

        return CursorSliceResponse.from(result, notification -> AdminNotificationListItemResponse.from(
            notification,
            toRentalSummary(notification, recordByHistoryId, memberById)
        ));
    }

    private static RentalSummary toRentalSummary(
        Notification notification, Map<Long, RentalRecord> recordByHistoryId, Map<Long, Member> memberById
    ) {
        if (!targetsRentalHistory(notification)) {
            return null;
        }
        RentalRecord record = recordByHistoryId.get(notification.getTargetId());
        if (record == null) {
            return null;
        }
        return RentalSummary.of(record, memberById.get(record.history().getMemberId()));
    }

    private static boolean targetsRentalHistory(Notification notification) {
        return RENTAL_TARGET_TYPES.contains(notification.getType()) && notification.getTargetId() != null;
    }
}
