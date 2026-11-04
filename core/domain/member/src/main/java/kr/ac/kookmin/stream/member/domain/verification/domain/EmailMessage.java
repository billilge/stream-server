package kr.ac.kookmin.stream.member.domain.verification.domain;

import java.util.Map;

public record EmailMessage(
    String to,
    String subject,
    EmailTemplate template,
    Map<String, Object> variables
) {}
