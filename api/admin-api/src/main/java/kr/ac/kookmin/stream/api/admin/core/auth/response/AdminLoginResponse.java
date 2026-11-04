package kr.ac.kookmin.stream.api.admin.core.auth.response;

import kr.ac.kookmin.stream.common.CouncilDepartment;

public record AdminLoginResponse(
    String accessToken,
    String name,
    CouncilDepartment councilDepartment
) {}
