package kr.ac.kookmin.stream.member.domain.verification.domain;

import java.util.Locale;

public record EmailVerificationSendCommand(String email) {

    public EmailVerificationSendCommand {
        email = email.toLowerCase(Locale.ROOT);
    }
}
