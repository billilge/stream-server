package kr.ac.kookmin.stream.api.app.core.notification.request;

import jakarta.validation.constraints.NotNull;
import kr.ac.kookmin.stream.member.domain.notification.domain.MemberNotificationSettingUpdateCommand;

public record MemberNotificationSettingUpdateRequest(
    @NotNull(message = "빌릴게 알림 수신 여부를 입력해 주세요.")
    Boolean rentalEnabled,

    @NotNull(message = "행사 알림 수신 여부를 입력해 주세요.")
    Boolean eventEnabled,

    @NotNull(message = "사물함 알림 수신 여부를 입력해 주세요.")
    Boolean lockerEnabled,

    @NotNull(message = "게시판 알림 수신 여부를 입력해 주세요.")
    Boolean noticeEnabled
) {

    public MemberNotificationSettingUpdateCommand toCommand() {
        return new MemberNotificationSettingUpdateCommand(rentalEnabled, eventEnabled, lockerEnabled, noticeEnabled);
    }
}
