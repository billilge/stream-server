package kr.ac.kookmin.stream.api.app.core.notification;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.core.notification.request.MemberNotificationSettingUpdateRequest;
import kr.ac.kookmin.stream.api.app.core.notification.response.MemberNotificationSettingResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.member.domain.notification.domain.MemberNotificationSetting;
import kr.ac.kookmin.stream.member.domain.notification.service.MemberNotificationSettingService;
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

    private final MemberNotificationSettingService memberNotificationSettingService;

    @Override
    @GetMapping("/settings")
    public ApiResponse<MemberNotificationSettingResponse> getSetting(AppApiUser apiUser) {
        MemberNotificationSetting setting = memberNotificationSettingService.getByMemberId(apiUser.userId());
        return ApiResponse.success(MemberNotificationSettingResponse.from(setting));
    }

    @Override
    @PutMapping("/settings")
    public ApiResponse<MemberNotificationSettingResponse> updateSetting(
        AppApiUser apiUser,
        @Valid @RequestBody MemberNotificationSettingUpdateRequest request
    ) {
        MemberNotificationSetting setting = memberNotificationSettingService.update(apiUser.userId(), request.toCommand());
        return ApiResponse.success(MemberNotificationSettingResponse.from(setting));
    }
}
