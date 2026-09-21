package kr.ac.kookmin.stream.api.admin.security;

import java.util.Set;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.common.CouncilDepartment;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * {@link RequireDepartment}가 붙은 컨트롤러 메서드의 부서 인가를 검증한다.
 * 인자 리졸빙이 끝난 뒤 실행되므로, 이미 주입된 {@link AdminApiUser}의 부서 목록으로 판정한다.
 */
@Aspect
@Component
public class RequireDepartmentAspect {

    // 회장단은 모든 부서 권한을 갖는 것으로 간주한다.
    private static final CouncilDepartment SUPER_DEPARTMENT = CouncilDepartment.PRESIDENCY;

    @Before("@annotation(requireDepartment)")
    public void check(JoinPoint joinPoint, RequireDepartment requireDepartment) {
        Set<CouncilDepartment> owned = findAdminApiUser(joinPoint).councilDepartments();

        if (owned.contains(SUPER_DEPARTMENT)) {
            return;
        }
        for (CouncilDepartment required : requireDepartment.value()) {
            if (owned.contains(required)) {
                return;
            }
        }
        throw new BusinessException(CommonErrorCode.FORBIDDEN);
    }

    private AdminApiUser findAdminApiUser(JoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof AdminApiUser adminApiUser) {
                return adminApiUser;
            }
        }
        // @RequireDepartment는 AdminApiUser로 검증하므로, 파라미터가 없으면 검증 자체가 불가능한 설정 오류다.
        throw new IllegalStateException(
            "@RequireDepartment가 붙은 메서드는 AdminApiUser 파라미터를 선언해야 합니다: " + joinPoint.getSignature()
        );
    }
}
