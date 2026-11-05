package kr.ac.kookmin.stream.api.app.core.notification.request;

import jakarta.validation.constraints.NotNull;

public record NotificationConsentUpdateRequest(
    @NotNull(message = "전체 알림 수신 동의 여부를 입력해 주세요.")
    Boolean agreed
) {
}
