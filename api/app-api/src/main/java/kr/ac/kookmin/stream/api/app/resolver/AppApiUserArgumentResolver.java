package kr.ac.kookmin.stream.api.app.resolver;

import kr.ac.kookmin.stream.api.common.resolver.ApiUserArgumentResolver;
import kr.ac.kookmin.stream.api.app.AppApiUser;
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
 * {@link AppApiUser} 파라미터를 주입한다.
 * <p>
 * SecurityConfig가 {@code /v1/app/**}를 STUDENT로 이미 1차 인가하므로 여기서의 role 검사는 2차 방어선이다.
 */
@Component
@RequiredArgsConstructor
public class AppApiUserArgumentResolver implements ApiUserArgumentResolver {

    private final PrincipalProvider principalProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return AppApiUser.class.equals(parameter.getParameterType());
    }

    @Override
    public AppApiUser resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) {
        if (!principalProvider.roles().contains(Role.STUDENT)) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }
        return AppApiUser.from(principalProvider.userId());
    }
}
