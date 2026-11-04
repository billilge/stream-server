package kr.ac.kookmin.stream.member.domain.verification.domain;

import java.util.Locale;

public record EmailVerificationConfirmCommand(String email, String code) {

    public EmailVerificationConfirmCommand {
        email = email.toLowerCase(Locale.ROOT);
    }
}
