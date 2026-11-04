package kr.ac.kookmin.stream.api.admin.core.auth.usecase;

import kr.ac.kookmin.stream.auth.domain.admin.domain.AdminAuthConfigKeys;
import kr.ac.kookmin.stream.auth.domain.admin.domain.AdminAuthErrorCode;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.internal.domain.config.service.AdminConfigValueService;
import kr.ac.kookmin.stream.security.PasswordHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 운영진 공용 비밀번호를 바꾼다. 현재 비밀번호가 맞을 때만 새 비밀번호의 해시로 덮어쓴다.
 * 이미 발급된 토큰은 만료될 때까지 그대로 쓸 수 있다.
 */
@Component
@RequiredArgsConstructor
public class SharedPasswordChangeUseCase {

    private final AdminConfigValueService adminConfigValueService;
    private final PasswordHasher passwordHasher;

    public void change(String currentPassword, String newPassword) {
        validateCurrentPassword(currentPassword);
        adminConfigValueService.upsertValue(AdminAuthConfigKeys.PASSWORD_HASH, passwordHasher.hash(newPassword));
    }

    private void validateCurrentPassword(String currentPassword) {
        String passwordHash = adminConfigValueService.getValue(AdminAuthConfigKeys.PASSWORD_HASH)
            .orElseThrow(() -> new IllegalStateException("운영진 공용 비밀번호가 설정되지 않았습니다."));
        if (!passwordHasher.matches(currentPassword, passwordHash)) {
            throw new BusinessException(AdminAuthErrorCode.ADMIN_PASSWORD_MISMATCH);
        }
    }
}
