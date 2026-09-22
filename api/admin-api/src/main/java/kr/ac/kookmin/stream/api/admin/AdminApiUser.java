package kr.ac.kookmin.stream.api.admin;

import java.util.Set;
import kr.ac.kookmin.stream.api.common.ApiUser;
import kr.ac.kookmin.stream.common.CouncilDepartment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class AdminApiUser implements ApiUser {

    private final Long userId;
    private final Set<CouncilDepartment> councilDepartments;

    public static AdminApiUser from(Long userId, Set<CouncilDepartment> councilDepartments) {
        return new AdminApiUser(userId, Set.copyOf(councilDepartments));
    }
}
