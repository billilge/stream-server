package kr.ac.kookmin.stream.api.admin.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import kr.ac.kookmin.stream.common.CouncilDepartment;

/**
 * 이 엔드포인트에 접근하려면 지정한 부서 중 하나 이상에 속해야 한다(any-of).
 * 회장단(PRESIDENCY)은 부서와 무관하게 항상 허용된다.
 * <p>
 * 검증은 {@code RequireDepartmentAspect}가 컨트롤러의 {@code AdminApiUser} 파라미터로 수행하므로,
 * 이 어노테이션을 붙인 메서드는 반드시 {@code AdminApiUser} 파라미터를 선언해야 한다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireDepartment {

    CouncilDepartment[] value();
}
