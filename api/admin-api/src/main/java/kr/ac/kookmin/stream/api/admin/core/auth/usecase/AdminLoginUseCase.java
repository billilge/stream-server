package kr.ac.kookmin.stream.api.admin.core.auth.usecase;

import kr.ac.kookmin.stream.api.admin.core.auth.response.AdminLoginResponse;
import kr.ac.kookmin.stream.auth.domain.admin.domain.AdminAuthConfigKeys;
import kr.ac.kookmin.stream.auth.domain.admin.domain.AdminAuthErrorCode;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.Role;
import kr.ac.kookmin.stream.internal.domain.config.service.AdminConfigValueService;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.security.AuthTokenIssuer;
import kr.ac.kookmin.stream.security.PasswordHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 학번과 운영진 공용 비밀번호로 로그인한다. role이 ADMIN인 회원만 로그인할 수 있고, 학생회 부서가 없어도 된다.
 * <p>
 * 실패 이유(회원 없음·운영진 아님·비밀번호 불일치)는 모두 같은 에러로 응답한다.
 * 비밀번호를 회원 조회보다 먼저 확인한다. 회원이 없을 때 BCrypt 비교를 건너뛰면 응답 시간 차이로 운영진 여부가 드러나기 때문이다.
 */
@Component
@RequiredArgsConstructor
public class AdminLoginUseCase {

    private final AdminConfigValueService adminConfigValueService;
    private final MemberService memberService;
    private final PasswordHasher passwordHasher;
    private final AuthTokenIssuer authTokenIssuer;

    public AdminLoginResponse login(String studentId, String password) {
        validatePassword(password);
        Member member = memberService.findByStudentId(studentId)
            .orElseThrow(() -> new BusinessException(AdminAuthErrorCode.INVALID_ADMIN_CREDENTIALS));
        validateAdmin(member);

        String accessToken = authTokenIssuer.issue(member.getId(), member.getRole(), member.getCouncilDepartment());
        return new AdminLoginResponse(accessToken, member.getName(), member.getCouncilDepartment());
    }

    private void validatePassword(String password) {
        // 해시가 없으면 아무도 로그인할 수 없는 운영 설정 누락이라 500으로 드러낸다
        String passwordHash = adminConfigValueService.getValue(AdminAuthConfigKeys.PASSWORD_HASH)
            .orElseThrow(() -> new IllegalStateException("운영진 공용 비밀번호가 설정되지 않았습니다."));
        if (!passwordHasher.matches(password, passwordHash)) {
            throw new BusinessException(AdminAuthErrorCode.INVALID_ADMIN_CREDENTIALS);
        }
    }

    private void validateAdmin(Member member) {
        if (member.getRole() != Role.ADMIN) {
            throw new BusinessException(AdminAuthErrorCode.INVALID_ADMIN_CREDENTIALS);
        }
    }
}
