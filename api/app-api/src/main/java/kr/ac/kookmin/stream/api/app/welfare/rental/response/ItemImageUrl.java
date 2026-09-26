package kr.ac.kookmin.stream.api.app.welfare.rental.response;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 물품 이미지 파일 키를 공개 URL로 바꾸는 지점.
 * <p>
 * 파일 키 → 공개 URL 조립이 아직 없어 현재는 항상 null이다. 조립이 생기면 이 메서드만 채우면 된다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ItemImageUrl {

    static String from(String imageKey) {
        return null;
    }
}
