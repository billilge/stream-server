package kr.ac.kookmin.stream.api.app.core.auth;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.app.core.auth.request.OAuthLoginRequest;
import kr.ac.kookmin.stream.api.app.core.auth.response.OAuthLoginResponse;
import kr.ac.kookmin.stream.api.app.core.auth.usecase.OAuthLoginUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AppAuthController implements AppAuthApi {

    private final OAuthLoginUseCase oauthLoginUseCase;

    @Override
    @PostMapping("/login/{provider}")
    public ApiResponse<OAuthLoginResponse> login(
        @PathVariable String provider,
        @Valid @RequestBody OAuthLoginRequest request
    ) {
        return ApiResponse.success(oauthLoginUseCase.login(request.toCommand(OAuthProvider.from(provider))));
    }
}
