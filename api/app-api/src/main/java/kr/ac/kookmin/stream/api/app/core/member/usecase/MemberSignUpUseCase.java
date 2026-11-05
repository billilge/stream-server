package kr.ac.kookmin.stream.api.app.core.member.usecase;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberSignUpCommand;
import kr.ac.kookmin.stream.member.domain.member.domain.TermType;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.member.domain.notification.service.MemberNotificationSettingService;
import kr.ac.kookmin.stream.member.domain.verification.domain.EmailVerificationErrorCode;
import kr.ac.kookmin.stream.member.domain.verification.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원가입. {@code member}(전화번호·이메일, 약관 동의), {@code verification}(이메일 인증 여부),
 * {@code notification}(기본 알림 설정) 세 도메인을 조합한다.
 */
@Component
@RequiredArgsConstructor
public class MemberSignUpUseCase {

    private final MemberService memberService;
    private final EmailVerificationService emailVerificationService;
    private final MemberNotificationSettingService memberNotificationSettingService;

    // 가입을 마친 회원에게는 항상 알림 설정이 있도록 한 트랜잭션으로 묶는다
    @Transactional
    public void signUp(Long memberId, MemberSignUpCommand command) {
        validateEmailVerified(memberId, command.email());
        memberService.signUp(memberId, command);
        boolean notificationConsentAgreed = command.hasAgreed(TermType.NOTIFICATION_CONSENT);
        memberNotificationSettingService.createDefaultIfAbsent(memberId, notificationConsentAgreed);
    }

    private void validateEmailVerified(Long memberId, String email) {
        boolean registeredEmail = email.equalsIgnoreCase(memberService.getById(memberId).getEmail());
        if (!registeredEmail && !emailVerificationService.isVerifiedForSignUp(memberId, email)) {
            throw new BusinessException(EmailVerificationErrorCode.EMAIL_NOT_VERIFIED);
        }
    }
}
