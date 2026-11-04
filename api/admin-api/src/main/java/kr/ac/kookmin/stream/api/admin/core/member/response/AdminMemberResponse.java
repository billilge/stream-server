package kr.ac.kookmin.stream.api.admin.core.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.ac.kookmin.stream.common.CouncilDepartment;
import kr.ac.kookmin.stream.common.Role;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;

public record AdminMemberResponse(
    @Schema(description = "회원 ID", example = "1")
    Long memberId,
    @Schema(description = "이름", example = "홍길동")
    String name,
    @Schema(description = "학번", example = "20211234")
    String studentId,
    @Schema(description = "이메일. 등록 전이면 null", example = "hong@kookmin.ac.kr")
    String email,
    @Schema(description = "학부", example = "소프트웨어학부")
    String department,
    @Schema(description = "역할")
    Role role,
    @Schema(description = "학생회 부서. 운영진이 아니거나 부서가 없으면 null")
    CouncilDepartment councilDepartment,
    @Schema(description = "회원가입(전화번호·필수 약관 동의)을 마쳤는지")
    boolean isSignedUp
) {

    public static AdminMemberResponse from(Member member, boolean isSignedUp) {
        return new AdminMemberResponse(
            member.getId(),
            member.getName(),
            member.getStudentId(),
            member.getEmail(),
            member.getDepartment().displayName(),
            member.getRole(),
            member.getCouncilDepartment(),
            isSignedUp
        );
    }
}
