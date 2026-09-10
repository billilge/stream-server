package kr.ac.kookmin.stream.admin;

import kr.ac.kookmin.stream.ApiUser;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * 운영진 콘솔 요청의 인증 사용자. 컨트롤러가 파라미터로 선언하면 AdminApiUserArgumentResolver가 주입한다.
 * <p>
 * record가 아니라 생성자를 감춘 클래스로 둔다. 리졸버가 없는 컨텍스트에서 스프링의 폴백
 * ServletModelAttributeMethodProcessor가 이 타입을 요청 파라미터로 생성하지 못하게 막기 위함이다.
 */
@Getter
@Accessors(fluent = true)
public final class AdminApiUser implements ApiUser {

    private final Long userId;

    private AdminApiUser(Long userId) {
        this.userId = userId;
    }

    public static AdminApiUser from(Long userId) {
        return new AdminApiUser(userId);
    }
}
