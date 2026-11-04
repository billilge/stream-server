package kr.ac.kookmin.stream.api.app.core.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.core.notification.request.MemberNotificationSettingUpdateRequest;
import kr.ac.kookmin.stream.api.app.core.notification.request.NotificationConsentUpdateRequest;
import kr.ac.kookmin.stream.api.app.core.notification.request.NotificationListParams;
import kr.ac.kookmin.stream.api.app.core.notification.response.MemberNotificationSettingResponse;
import kr.ac.kookmin.stream.api.app.core.notification.response.NotificationListItemResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.CursorSliceResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.member.domain.notification.domain.NotificationErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 학생 앱 알림 API의 문서 명세. 구현은 {@link AppNotificationController}가 맡는다.
 */
@Tag(name = "알림", description = "학생 앱 알림함·알림 설정")
public interface AppNotificationApi {

    /** 알림함 목록. */
    @Operation(summary = "알림함 조회",
        description = "커서 기반으로 내 알림을 최신순으로 조회한다. cursor/size로 다음 페이지를 넘긴다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = NotificationErrorCode.class, codes = {"NOTIFICATION_INVALID_CURSOR"})
    ApiResponse<CursorSliceResponse<NotificationListItemResponse>> getNotifications(
        AppApiUser apiUser,
        @ParameterObject NotificationListParams params
    );

    /** 알림 읽음 처리. */
    @Operation(summary = "알림 읽음 처리",
        description = "알림 한 건을 읽음으로 바꾼다. 이미 읽은 알림이면 그대로 성공한다.")
    @ApiErrorCode(type = NotificationErrorCode.class, codes = {"NOTIFICATION_NOT_FOUND"})
    ApiResponse<Void> markRead(AppApiUser apiUser, Long notificationId);

    /** 알림 설정 조회. */
    @Operation(summary = "알림 설정 조회",
        description = """
            로그인한 회원의 전체 알림 수신 동의(notificationConsentAgreed)와 카테고리별 푸시 수신 여부를 조회한다.
            rentalEnabled는 빌릴게, eventEnabled는 행사, lockerEnabled는 사물함, noticeEnabled는 게시판(새 공지·열린피드백 답변) 알림이다.
            동의하지 않으면 카테고리 설정과 무관하게 푸시가 나가지 않는다.""")
    @ApiErrorCode(type = NotificationErrorCode.class, codes = {"NOTIFICATION_SETTING_NOT_FOUND"})
    ApiResponse<MemberNotificationSettingResponse> getSetting(AppApiUser apiUser);

    /** 알림 설정 수정. */
    @Operation(summary = "알림 설정 수정",
        description = """
            로그인한 회원의 카테고리별 푸시 수신 여부를 바꾼다. 네 값을 모두 보내야 하고, 보낸 값으로 덮어쓴다.
            전체 알림 수신 동의 자체는 바꾸지 않는다(동의 변경 API 참고).
            꺼진 카테고리의 알림은 푸시로 보내지 않지만 알림함에는 남는다.""")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = NotificationErrorCode.class, codes = {"NOTIFICATION_SETTING_NOT_FOUND"})
    ApiResponse<MemberNotificationSettingResponse> updateSetting(
        AppApiUser apiUser,
        MemberNotificationSettingUpdateRequest request
    );

    /** 전체 알림 수신 동의 변경. */
    @Operation(summary = "전체 알림 수신 동의 변경",
        description = """
            전체 알림 수신 동의(NOTIFICATION_CONSENT 약관) 자체를 켜거나 끈다. 가입할 때 한 번만 받던 값을 가입 뒤에도 바꿀 수 있게 한다.
            동의를 끄면 카테고리 설정과 무관하게 푸시가 전부 나가지 않는다. 카테고리별 설정 값은 그대로 유지되지만,
            동의를 켤 때 네 카테고리가 모두 꺼져 있으면(가입할 때 동의하지 않은 경우) 함께 켠다.""")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = NotificationErrorCode.class, codes = {"NOTIFICATION_SETTING_NOT_FOUND"})
    ApiResponse<MemberNotificationSettingResponse> updateConsent(
        AppApiUser apiUser,
        NotificationConsentUpdateRequest request
    );
}
