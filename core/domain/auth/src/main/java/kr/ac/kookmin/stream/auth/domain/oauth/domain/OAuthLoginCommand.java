package kr.ac.kookmin.stream.auth.domain.oauth.domain;

public record OAuthLoginCommand(
    OAuthProvider provider,
    String code,
    String codeVerifier,
    String redirectUri
) {}
