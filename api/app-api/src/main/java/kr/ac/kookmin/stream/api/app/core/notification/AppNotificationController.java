package kr.ac.kookmin.stream.api.app.core.notification;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.core.notification.request.MemberNotificationSettingUpdateRequest;
import kr.ac.kookmin.stream.api.app.core.notification.request.NotificationConsentUpdateRequest;
import kr.ac.kookmin.stream.api.app.core.notification.request.NotificationListParams;
import kr.ac.kookmin.stream.api.app.core.notification.response.MemberNotificationSettingResponse;
import kr.ac.kookmin.stream.api.app.core.notification.response.NotificationListItemResponse;
import kr.ac.kookmin.stream.api.app.core.notification.usecase.NotificationSettingUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.CursorSliceResponse;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.member.domain.notification.domain.Notification;
import kr.ac.kookmin.stream.member.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/app/notifications")
@RequiredArgsConstructor
public class AppNotificationController implements AppNotificationApi {

    private final NotificationService notificationService;
    private final NotificationSettingUseCase notificationSettingUseCase;

    @Override
    @GetMapping
    public ApiResponse<CursorSliceResponse<NotificationListItemResponse>> getNotifications(
        AppApiUser apiUser,
        @Valid @ModelAttribute NotificationListParams params
    ) {
        CursorSliceResult<Notification> result = notificationService.getNotifications(
            apiUser.userId(), params.toCursor(), params.sizeOrDefault());
        return ApiResponse.success(CursorSliceResponse.from(result, NotificationListItemResponse::from));
    }

    @Override
    @PatchMapping("/{notificationId}/read")
    public ApiResponse<Void> markRead(AppApiUser apiUser, @PathVariable Long notificationId) {
        notificationService.markRead(apiUser.userId(), notificationId);
        return ApiResponse.success();
    }

    @Override
    @GetMapping("/settings")
    public ApiResponse<MemberNotificationSettingResponse> getSetting(AppApiUser apiUser) {
        return ApiResponse.success(MemberNotificationSettingResponse.from(
            notificationSettingUseCase.getSetting(apiUser.userId())));
    }

    @Override
    @PutMapping("/settings")
    public ApiResponse<MemberNotificationSettingResponse> updateSetting(
        AppApiUser apiUser,
        @Valid @RequestBody MemberNotificationSettingUpdateRequest request
    ) {
        return ApiResponse.success(MemberNotificationSettingResponse.from(
            notificationSettingUseCase.updateSetting(apiUser.userId(), request.toCommand())));
    }

    @Override
    @PutMapping("/consent")
    public ApiResponse<MemberNotificationSettingResponse> updateConsent(
        AppApiUser apiUser,
        @Valid @RequestBody NotificationConsentUpdateRequest request
    ) {
        return ApiResponse.success(MemberNotificationSettingResponse.from(
            notificationSettingUseCase.updateConsent(apiUser.userId(), request.agreed())));
    }
}
