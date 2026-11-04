package kr.ac.kookmin.stream.api.app.core.notification;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.core.notification.request.MemberNotificationSettingUpdateRequest;
import kr.ac.kookmin.stream.api.app.core.notification.request.NotificationConsentUpdateRequest;
import kr.ac.kookmin.stream.api.app.core.notification.response.MemberNotificationSettingResponse;
import kr.ac.kookmin.stream.api.app.core.notification.usecase.NotificationSettingUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/app/notifications")
@RequiredArgsConstructor
public class AppNotificationController implements AppNotificationApi {

    private final NotificationSettingUseCase notificationSettingUseCase;

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
