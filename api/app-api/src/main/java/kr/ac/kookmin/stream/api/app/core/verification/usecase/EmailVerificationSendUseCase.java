package kr.ac.kookmin.stream.api.app.core.verification.usecase;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberErrorCode;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.member.domain.verification.domain.EmailVerificationSendCommand;
import kr.ac.kookmin.stream.member.domain.verification.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 이메일 인증 코드 발송. {@code member}(이메일 중복 확인)와 {@code verification}(코드 발송) 두 도메인을 조합한다.
 */
@Component
@RequiredArgsConstructor
public class EmailVerificationSendUseCase {

    private final MemberService memberService;
    private final EmailVerificationService emailVerificationService;

    public void sendCode(Long memberId, EmailVerificationSendCommand command) {
        validateEmailNotUsedByOthers(memberId, command.email());
        emailVerificationService.sendCode(memberId, command);
    }

    private void validateEmailNotUsedByOthers(Long memberId, String email) {
        if (memberService.isEmailUsedByOthers(memberId, email)) {
            throw new BusinessException(MemberErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }
}
