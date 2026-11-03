package kr.ac.kookmin.stream.api.admin.core.auth;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.admin.core.auth.request.AdminLoginRequest;
import kr.ac.kookmin.stream.api.admin.core.auth.response.AdminLoginResponse;
import kr.ac.kookmin.stream.api.admin.core.auth.usecase.AdminLoginUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController implements AdminAuthApi {

    private final AdminLoginUseCase adminLoginUseCase;

    @Override
    @PostMapping("/login")
    public ApiResponse<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        return ApiResponse.success(adminLoginUseCase.login(request.studentId(), request.password()));
    }
}
