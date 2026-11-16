package kr.ac.kookmin.stream.api.app.core.auth.usecase;

import java.util.Arrays;
import kr.ac.kookmin.stream.api.app.core.auth.response.OAuthLoginResponse;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthLoginCommand;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthUserInfo;
import kr.ac.kookmin.stream.auth.domain.oauth.service.OAuthService;
import kr.ac.kookmin.stream.internal.domain.config.service.AdminConfigValueService;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberConfigKeys;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberProfileCommand;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.security.AuthTokenIssuer;
import kr.ac.kookmin.stream.welfare.domain.fee.service.PayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * provider 로그인으로 사용자를 확인하고, 회원을 찾거나 만들어 서비스 토큰을 발급한다.
 * <p>
 * 전체를 한 트랜잭션으로 묶지 않는다. 묶으면 provider를 호출하는 동안 DB 커넥션을 잡고 있게 된다.
 * 가입 뒤 계정 연결이 실패해도 다음 로그인에서 학번으로 회원을 찾아 다시 연결하므로 회원이 고아로 남지 않는다.
 */
@Component
@RequiredArgsConstructor
public class OAuthLoginUseCase {

    // 소속 검사 예외 학번에 넣어 주는 전공. 학부는 SW로 저장된다
    private static final String BYPASS_MAJOR = "소프트웨어전공";

    private final AdminConfigValueService adminConfigValueService;
    private final OAuthService oauthService;
    private final MemberService memberService;
    private final PayerService payerService;
    private final AuthTokenIssuer authTokenIssuer;

    public OAuthLoginResponse login(OAuthLoginCommand command) {
        OAuthUserInfo userInfo = oauthService.authenticate(command);
        Member member = findOrRegister(userInfo);
        // 미가입 레거시 납부자가 가입하면 로그인마다 명부 행과 연결을 시도한다. 이미 연결됐으면 바로 끝난다
        payerService.linkMember(member.getId(), member.getStudentId());

        String accessToken = authTokenIssuer.issue(member.getId(), member.getRole(), member.getCouncilDepartment());
        // 회원을 이번에 만들었는지가 아니라 저장된 전화번호·이메일·동의 기록으로 판단한다. 회원가입 화면에서 이탈한 회원도 다시 가입 화면을 본다
        boolean signUpRequired = !memberService.isSignedUp(member.getId());
        return new OAuthLoginResponse(accessToken, member.getName(), signUpRequired);
    }

    private Member findOrRegister(OAuthUserInfo userInfo) {
        String major = isDepartmentCheckBypassed(userInfo.studentId()) ? BYPASS_MAJOR : userInfo.major();
        MemberProfileCommand profile = new MemberProfileCommand(
            userInfo.studentId(), userInfo.name(), major, userInfo.academicStatus());

        return oauthService.findMemberId(userInfo.provider(), userInfo.providerUserId())
            .map(memberId -> memberService.updateProfile(memberId, profile))
            .orElseGet(() -> registerAndLink(userInfo, profile));
    }

    // 설정값에 등록된 학번이면 소속 검사를 건너뛴다
    private boolean isDepartmentCheckBypassed(String studentId) {
        if (studentId == null) {
            return false;
        }
        return adminConfigValueService.getValue(MemberConfigKeys.DEPARTMENT_BYPASS_STUDENT_IDS)
            .map(value -> Arrays.stream(value.split(",")).map(String::trim).anyMatch(studentId::equals))
            .orElse(false);
    }

    // 연결된 계정이 없으면 학번 기준으로 회원을 가입·갱신한다. 이관 회원이나 연결이 누락된 회원은 여기서 다시 연결된다
    private Member registerAndLink(OAuthUserInfo userInfo, MemberProfileCommand profile) {
        Member member = memberService.registerOrUpdateByStudentId(profile);
        oauthService.link(userInfo.provider(), userInfo.providerUserId(), member.getId());
        return member;
    }
}
