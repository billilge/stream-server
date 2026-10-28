package kr.ac.kookmin.stream.api.app.core.member.usecase;

import kr.ac.kookmin.stream.member.domain.member.domain.MemberSignUpCommand;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.member.domain.notification.service.MemberNotificationSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원가입. {@code member}(전화번호, 약관 동의)와 {@code notification}(기본 알림 설정) 두 도메인을 조합한다.
 */
@Component
@RequiredArgsConstructor
public class MemberSignUpUseCase {

    private final MemberService memberService;
    private final MemberNotificationSettingService memberNotificationSettingService;

    // 가입을 마친 회원에게는 항상 알림 설정이 있도록 한 트랜잭션으로 묶는다
    @Transactional
    public void signUp(Long memberId, MemberSignUpCommand command) {
        memberService.signUp(memberId, command);
        memberNotificationSettingService.createDefaultIfAbsent(memberId);
    }
}
