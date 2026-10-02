package kr.ac.kookmin.stream.member.domain.notification.domain;

public record PushSendOutcome(String token, PushSendStatus status) {}
