package kr.ac.kookmin.stream.api.admin.core.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.core.notification.request.AdminNotificationListParams;
import kr.ac.kookmin.stream.api.admin.core.notification.response.AdminNotificationListItemResponse;
import kr.ac.kookmin.stream.api.admin.core.notification.response.AdminNotificationUnreadResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.CursorSliceResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.member.domain.notification.domain.NotificationErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 운영진 공용 알림함(member_id가 없는 행)의 문서 명세. 구현은 {@link AdminNotificationController}가 맡는다.
 * 읽음 여부는 운영진 전체가 공유한다 — 개인별 알림함이 아니다.
 */
@Tag(name = "알림", description = "운영진 공용 알림함")
public interface AdminNotificationApi {

    /** 운영진 공용 알림함 목록. */
    @Operation(summary = "운영진 공용 알림함 조회",
        description = "커서 기반으로 운영진 공용 알림을 최신순으로 조회한다. cursor/size로 다음 페이지를 넘긴다. "
            + "대여 알림(ADMIN_RENTAL_APPLY, ADMIN_RENTAL_CANCEL, ADMIN_RETURN_APPLY)에는 targetId가 가리키는 대여 기록의 "
            + "지금 값(rental: 물품명·이미지·수량·신청자 이름·대여 시각·대여 상태)을 함께 싣는다. title·body는 푸시와 같은 "
            + "문구이고 만들어질 때 고정되므로, 화면의 대여 건 표시와 버튼은 rental로 고른다. 대여 알림이 아니거나 대여 기록이 "
            + "지워졌으면 rental은 null이다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = NotificationErrorCode.class, codes = {"NOTIFICATION_INVALID_CURSOR"})
    ApiResponse<CursorSliceResponse<AdminNotificationListItemResponse>> getNotifications(
        AdminApiUser apiUser,
        @ParameterObject AdminNotificationListParams params
    );

    /** 운영진 공용 알림 읽음 처리. */
    @Operation(summary = "운영진 공용 알림 읽음 처리",
        description = "알림 한 건을 읽음으로 바꾼다. 운영진 전체가 공유하는 행이라 누가 처리해도 전체가 읽음으로 본다.")
    @ApiErrorCode(type = NotificationErrorCode.class, codes = {"NOTIFICATION_NOT_FOUND"})
    ApiResponse<Void> markRead(AdminApiUser apiUser, Long notificationId);

    /** 운영진 공용 알림 중 안 읽은 알림 존재 여부. */
    @Operation(summary = "운영진 공용 안 읽은 알림 여부 조회",
        description = "운영진 공용 알림 중 안 읽은 알림이 하나라도 있으면 hasUnread가 true다. 읽음 여부는 운영진 전체가 공유한다.")
    ApiResponse<AdminNotificationUnreadResponse> getUnread(AdminApiUser apiUser);
}
