package kr.ac.kookmin.stream.api.common;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CommonErrorCode;

// 커서 문자열을 클라이언트에게 불투명한 토큰으로 감싼다. 실제 정렬 키 파싱은 각 도메인이 담당하고,
// 여기서는 웹(쿼리 파라미터)으로 오가는 형태(Base64 URL-safe)만 다룬다.
public final class CursorCodec {

    private CursorCodec() {}

    public static String encode(String raw) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static String decode(String cursor) {
        try {
            return new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT);
        }
    }
}
