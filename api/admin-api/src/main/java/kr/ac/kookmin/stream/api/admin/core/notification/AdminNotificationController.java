package kr.ac.kookmin.stream.api.admin.core.notification;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.core.notification.request.AdminNotificationListParams;
import kr.ac.kookmin.stream.api.admin.core.notification.response.AdminNotificationListItemResponse;
import kr.ac.kookmin.stream.api.admin.core.notification.response.AdminNotificationUnreadResponse;
import kr.ac.kookmin.stream.api.admin.core.notification.usecase.AdminNotificationListUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.CursorSliceResponse;
import kr.ac.kookmin.stream.member.domain.notification.service.AdminNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/notifications")
@RequiredArgsConstructor
public class AdminNotificationController implements AdminNotificationApi {

    private final AdminNotificationService adminNotificationService;
    private final AdminNotificationListUseCase adminNotificationListUseCase;

    @Override
    @GetMapping
    public ApiResponse<CursorSliceResponse<AdminNotificationListItemResponse>> getNotifications(
        AdminApiUser apiUser,
        @Valid @ModelAttribute AdminNotificationListParams params
    ) {
        return ApiResponse.success(
            adminNotificationListUseCase.getNotifications(params.toCursor(), params.sizeOrDefault()));
    }

    @Override
    @PatchMapping("/{notificationId}/read")
    public ApiResponse<Void> markRead(AdminApiUser apiUser, @PathVariable Long notificationId) {
        adminNotificationService.markRead(notificationId);
        return ApiResponse.success();
    }

    @Override
    @GetMapping("/unread")
    public ApiResponse<AdminNotificationUnreadResponse> getUnread(AdminApiUser apiUser) {
        boolean hasUnread = adminNotificationService.hasUnread();
        return ApiResponse.success(new AdminNotificationUnreadResponse(hasUnread));
    }
}
