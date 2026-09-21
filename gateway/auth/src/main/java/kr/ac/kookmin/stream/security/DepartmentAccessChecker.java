package kr.ac.kookmin.stream.security;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.common.CouncilDepartment;
import kr.ac.kookmin.stream.common.PrincipalProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DepartmentAccessChecker {

    private final PrincipalProvider principalProvider;

    public void requireDepartment(CouncilDepartment required) {
        if (!principalProvider.councilDepartments().contains(required)) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }
    }
}
