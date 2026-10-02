package kr.ac.kookmin.stream.api.app.core.auth.response;

/**
 * @param termsAgreementRequired 필수 약관에 아직 동의하지 않았으면 true. 앱은 이 값으로 약관 화면을 띄운다
 */
public record OAuthLoginResponse(
    String accessToken,
    boolean termsAgreementRequired
) {}
