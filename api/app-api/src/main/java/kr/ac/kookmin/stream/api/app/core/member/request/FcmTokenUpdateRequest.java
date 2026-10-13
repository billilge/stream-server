package kr.ac.kookmin.stream.api.app.core.member.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record FcmTokenUpdateRequest(
    // FCM 등록 토큰({인스턴스 ID}:APA91b...)과 FID 모두 base64url 문자와 ':'만 쓴다. 길이는 members.fcm_token 컬럼에 맞춘다
    @NotBlank(message = "FCM 토큰을 입력해 주세요.")
    @Size(max = 255, message = "FCM 토큰은 255자 이하여야 합니다.")
    @Pattern(regexp = "^[A-Za-z0-9_:-]+$", message = "FCM 토큰 형식이 올바르지 않습니다.")
    String fcmToken
) {}
