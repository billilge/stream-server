package kr.ac.kookmin.stream.api.admin.core.auth;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.core.auth.request.AdminLoginRequest;
import kr.ac.kookmin.stream.api.admin.core.auth.request.SharedPasswordChangeRequest;
import kr.ac.kookmin.stream.api.admin.core.auth.response.AdminLoginResponse;
import kr.ac.kookmin.stream.api.admin.core.auth.usecase.AdminLoginUseCase;
import kr.ac.kookmin.stream.api.admin.core.auth.usecase.SharedPasswordChangeUseCase;
import kr.ac.kookmin.stream.api.admin.security.RequireDepartment;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.common.CouncilDepartment;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController implements AdminAuthApi {

    private final AdminLoginUseCase adminLoginUseCase;
    private final SharedPasswordChangeUseCase sharedPasswordChangeUseCase;

    @Override
    @PostMapping("/login")
    public ApiResponse<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        return ApiResponse.success(adminLoginUseCase.login(request.studentId(), request.password()));
    }

    @Override
    @PutMapping("/shared-password")
    @RequireDepartment(CouncilDepartment.PRESIDENCY)
    public ApiResponse<Void> changeSharedPassword(
        AdminApiUser apiUser,
        @Valid @RequestBody SharedPasswordChangeRequest request
    ) {
        sharedPasswordChangeUseCase.change(request.currentPassword(), request.newPassword());
        return ApiResponse.success();
    }
}
