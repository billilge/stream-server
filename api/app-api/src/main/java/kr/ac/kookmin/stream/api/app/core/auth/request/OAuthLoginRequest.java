package kr.ac.kookmin.stream.api.app.core.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthLoginCommand;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthProvider;

public record OAuthLoginRequest(
    @NotBlank(message = "인가 코드를 입력해 주세요.")
    String code,

    // PKCE code verifier 규격 (RFC 7636)
    @NotBlank(message = "code verifier를 입력해 주세요.")
    @Pattern(regexp = "^[A-Za-z0-9._~-]{43,128}$", message = "code verifier 형식이 올바르지 않습니다.")
    String codeVerifier,

    @NotBlank(message = "redirect URI를 입력해 주세요.")
    String redirectUri
) {

    public OAuthLoginCommand toCommand(OAuthProvider provider) {
        return new OAuthLoginCommand(provider, code, codeVerifier, redirectUri);
    }
}
