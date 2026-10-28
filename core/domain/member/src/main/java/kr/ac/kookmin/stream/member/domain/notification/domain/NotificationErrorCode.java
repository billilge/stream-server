package kr.ac.kookmin.stream.member.domain.notification.domain;

import kr.ac.kookmin.stream.common.ErrorCode;
import kr.ac.kookmin.stream.common.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

    NOTIFICATION_SETTING_NOT_FOUND(ErrorStatus.NOT_FOUND, "알림 설정을 찾을 수 없습니다.");

    private final int status;
    private final String message;
}
