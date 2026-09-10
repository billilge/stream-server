package kr.ac.kookmin.stream.admin.resolver;

import kr.ac.kookmin.stream.ApiUserArgumentResolver;
import kr.ac.kookmin.stream.admin.AdminApiUser;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.common.PrincipalProvider;
import kr.ac.kookmin.stream.common.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * {@link AdminApiUser} 파라미터를 주입한다.
 * <p>
 * SecurityConfig가 {@code /v1/admin/**}를 ADMIN으로 이미 1차 인가하므로 여기서의 role 검사는 2차 방어선이다.
 */
@Component
@RequiredArgsConstructor
public class AdminApiUserArgumentResolver implements ApiUserArgumentResolver {

    private final PrincipalProvider principalProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return AdminApiUser.class.equals(parameter.getParameterType());
    }

    @Override
    public AdminApiUser resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) {
        if (!principalProvider.roles().contains(Role.ADMIN)) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }
        return AdminApiUser.from(principalProvider.userId());
    }
}
