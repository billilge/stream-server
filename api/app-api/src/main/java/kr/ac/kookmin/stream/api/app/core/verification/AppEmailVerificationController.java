package kr.ac.kookmin.stream.api.app.core.verification;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.core.verification.request.EmailVerificationConfirmRequest;
import kr.ac.kookmin.stream.api.app.core.verification.request.EmailVerificationSendRequest;
import kr.ac.kookmin.stream.api.app.core.verification.usecase.EmailVerificationSendUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.member.domain.verification.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/app/members/email-verifications")
@RequiredArgsConstructor
public class AppEmailVerificationController implements AppEmailVerificationApi {

    private final EmailVerificationService emailVerificationService;
    private final EmailVerificationSendUseCase emailVerificationSendUseCase;

    @Override
    @PostMapping
    public ApiResponse<Void> sendCode(
        AppApiUser apiUser,
        @Valid @RequestBody EmailVerificationSendRequest request
    ) {
        emailVerificationSendUseCase.sendCode(apiUser.userId(), request.toCommand());
        return ApiResponse.success();
    }

    @Override
    @PostMapping("/confirm")
    public ApiResponse<Void> confirm(
        AppApiUser apiUser,
        @Valid @RequestBody EmailVerificationConfirmRequest request
    ) {
        emailVerificationService.confirm(apiUser.userId(), request.toCommand());
        return ApiResponse.success();
    }
}
