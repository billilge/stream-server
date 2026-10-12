package kr.ac.kookmin.stream.api.app.core.auth.response;

/**
 * @param signUpRequired 회원가입(전화번호·필수 약관 동의)을 마치지 않았으면 true. 앱은 이 값으로 회원가입 화면을 띄운다
 */
public record OAuthLoginResponse(
    String accessToken,
    String name,
    boolean signUpRequired
) {}
