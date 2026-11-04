package kr.ac.kookmin.stream.api.admin.core.notification;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.admin.core.notification.request.AdminNotificationListParams;
import kr.ac.kookmin.stream.api.admin.core.notification.response.AdminNotificationListItemResponse;
import kr.ac.kookmin.stream.api.admin.core.notification.response.AdminNotificationUnreadResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.CursorSliceResponse;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.member.domain.notification.domain.Notification;
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

    @Override
    @GetMapping
    public ApiResponse<CursorSliceResponse<AdminNotificationListItemResponse>> getNotifications(
        @Valid @ModelAttribute AdminNotificationListParams params
    ) {
        CursorSliceResult<Notification> result = adminNotificationService.getNotifications(
            params.toCursor(), params.sizeOrDefault());
        return ApiResponse.success(CursorSliceResponse.from(result, AdminNotificationListItemResponse::from));
    }

    @Override
    @PatchMapping("/{notificationId}/read")
    public ApiResponse<Void> markRead(@PathVariable Long notificationId) {
        adminNotificationService.markRead(notificationId);
        return ApiResponse.success();
    }

    @Override
    @GetMapping("/unread")
    public ApiResponse<AdminNotificationUnreadResponse> getUnread() {
        boolean hasUnread = adminNotificationService.hasUnread();
        return ApiResponse.success(new AdminNotificationUnreadResponse(hasUnread));
    }
}
