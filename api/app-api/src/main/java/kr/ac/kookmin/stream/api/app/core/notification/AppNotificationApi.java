package kr.ac.kookmin.stream.api.app.core.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.core.notification.request.MemberNotificationSettingUpdateRequest;
import kr.ac.kookmin.stream.api.app.core.notification.response.MemberNotificationSettingResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.member.domain.notification.domain.NotificationErrorCode;

/**
 * 학생 앱 알림 API의 문서 명세. 구현은 {@link AppNotificationController}가 맡는다.
 */
@Tag(name = "알림", description = "학생 앱 알림 설정")
public interface AppNotificationApi {

    /** 알림 설정 조회. */
    @Operation(summary = "알림 설정 조회",
        description = """
            로그인한 회원의 카테고리별 푸시 수신 여부를 조회한다.
            rentalEnabled는 빌릴게, eventEnabled는 행사, lockerEnabled는 사물함, noticeEnabled는 게시판(새 공지·열린피드백 답변) 알림이다.
            전체 알림은 따로 저장하지 않는다. 네 값이 모두 true일 때 켜진 것으로 보여준다.""")
    @ApiErrorCode(type = NotificationErrorCode.class, codes = {"NOTIFICATION_SETTING_NOT_FOUND"})
    ApiResponse<MemberNotificationSettingResponse> getSetting(AppApiUser apiUser);

    /** 알림 설정 수정. */
    @Operation(summary = "알림 설정 수정",
        description = """
            로그인한 회원의 카테고리별 푸시 수신 여부를 바꾼다. 네 값을 모두 보내야 하고, 보낸 값으로 덮어쓴다.
            전체 알림을 켜거나 끌 때는 네 값을 모두 true나 false로 보낸다.
            꺼진 카테고리의 알림은 푸시로 보내지 않지만 알림함에는 남는다.""")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = NotificationErrorCode.class, codes = {"NOTIFICATION_SETTING_NOT_FOUND"})
    ApiResponse<MemberNotificationSettingResponse> updateSetting(
        AppApiUser apiUser,
        MemberNotificationSettingUpdateRequest request
    );
}
