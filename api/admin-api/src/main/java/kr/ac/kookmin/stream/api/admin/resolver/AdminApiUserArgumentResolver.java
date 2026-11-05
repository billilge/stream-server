package kr.ac.kookmin.stream.api.admin.resolver;

import kr.ac.kookmin.stream.api.common.resolver.ApiUserArgumentResolver;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import java.util.EnumSet;
import java.util.Set;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.common.CouncilDepartment;
import kr.ac.kookmin.stream.common.PrincipalProvider;
import kr.ac.kookmin.stream.common.Role;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
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
 * <p>
 * 토큰은 발급 시점의 값이라, 탈퇴·권한 회수·부서 변경이 만료 전까지 반영되지 않는다. 그래서 회원을 DB에서 다시 읽어
 * 탈퇴했으면 401(재로그인 유도), 더 이상 ADMIN이 아니면 403으로 막고, 부서도 DB 값으로 채운다.
 */
@Component
@RequiredArgsConstructor
public class AdminApiUserArgumentResolver implements ApiUserArgumentResolver {

    private final PrincipalProvider principalProvider;
    private final MemberService memberService;

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
        Member member = memberService.findById(principalProvider.userId())
            .orElseThrow(() -> new BusinessException(CommonErrorCode.UNAUTHORIZED));
        if (member.getRole() != Role.ADMIN) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }
        return AdminApiUser.from(member.getId(), councilDepartmentsOf(member));
    }

    private Set<CouncilDepartment> councilDepartmentsOf(Member member) {
        CouncilDepartment councilDepartment = member.getCouncilDepartment();
        return councilDepartment == null ? EnumSet.noneOf(CouncilDepartment.class) : EnumSet.of(councilDepartment);
    }
}
