package kr.ac.kookmin.stream.api.admin.core.notification.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.api.common.ApiConstants;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.notification.domain.Notification;
import kr.ac.kookmin.stream.member.domain.notification.domain.NotificationType;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;

public record AdminNotificationListItemResponse(
    Long notificationId,
    NotificationType type,
    String title,
    String body,
    Long targetId,
    boolean read,
    LocalDateTime createdAt,
    // targetId가 가리키는 대여 기록의 지금 값. 대여 알림이 아니거나 대여 기록이 지워졌으면 null
    RentalSummary rental
) {

    public static AdminNotificationListItemResponse from(Notification notification, RentalSummary rental) {
        return new AdminNotificationListItemResponse(
            notification.getId(),
            notification.getType(),
            notification.getType().formatTitle(notification.getFormatValueList()),
            notification.getType().formatBody(notification.getFormatValueList()),
            notification.getTargetId(),
            notification.isRead(),
            notification.getCreatedAt(),
            rental
        );
    }

    /** 알림 화면이 대여 건을 그리고 버튼을 고를 때 쓰는 대여 기록 요약. 알림이 만들어진 시점이 아니라 지금 값이다. */
    public record RentalSummary(
        Long rentalHistoryId,
        // 물품이 삭제됐으면 null
        String itemName,
        // 물품이 삭제됐거나 이미지가 없으면 null
        String itemImageUrl,
        int rentedCount,
        // 탈퇴(소프트 삭제)한 회원이면 탈퇴 회원 표시 문구
        String memberName,
        // PENDING·CONFIRMED는 학생이 고른 희망 시각, RENTAL부터는 실제 수령 시각
        LocalDateTime rentAt,
        RentalStatus rentalStatus
    ) {

        public static RentalSummary of(RentalRecord record, Member member) {
            RentalHistory history = record.history();
            return new RentalSummary(
                history.getId(),
                record.itemName(),
                StorageUrlBuilder.build(record.itemImageKey()),
                history.getRentedCount(),
                member != null ? member.getName() : ApiConstants.WITHDRAWN_MEMBER_LABEL,
                history.getRentAt(),
                history.getRentalStatus()
            );
        }
    }
}
