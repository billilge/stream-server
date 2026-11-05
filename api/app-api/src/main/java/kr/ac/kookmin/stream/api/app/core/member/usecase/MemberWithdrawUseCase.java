package kr.ac.kookmin.stream.api.app.core.member.usecase;

import kr.ac.kookmin.stream.auth.domain.oauth.service.OAuthService;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MemberWithdrawUseCase {

    private final MemberService memberService;
    private final OAuthService oauthService;

    // 로그인은 provider 계정에 연결된 회원 ID로 회원을 찾으므로, 연결이 남으면 탈퇴한 회원을 찾다가 다시 로그인할 수 없다.
    // 회원 탈퇴와 연결 해제를 한 트랜잭션으로 묶는다
    @Transactional
    public void withdraw(Long memberId) {
        memberService.withdraw(memberId);
        oauthService.unlinkAll(memberId);
    }
}
