package kr.ac.kookmin.stream.api.admin.core.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import kr.ac.kookmin.stream.common.CouncilDepartment;
import kr.ac.kookmin.stream.common.Role;
import kr.ac.kookmin.stream.member.domain.member.domain.CouncilUpdateCommand;

/** 관리자 한 명을 고치는 화면과 권한을 일괄로 바꾸는 화면이 같이 쓴다. 전자만 부서를 보낸다. */
public record CouncilUpdateRequest(

    @NotEmpty(message = "변경할 회원을 한 명 이상 선택해 주세요.")
    @Schema(description = "변경할 회원 식별자", example = "[1, 2]")
    List<@NotNull(message = "회원 식별자는 비워 둘 수 없습니다.") Long> memberIds,

    @NotNull(message = "변경할 권한을 선택해 주세요.")
    @Schema(description = "바꿀 권한", example = "ADMIN")
    Role role,

    @Schema(description = "바꿀 학생회 부서. 보내지 않으면 각자의 지금 부서를 그대로 둔다", example = "PLANNING")
    CouncilDepartment councilDepartment
) {

    public CouncilUpdateCommand toCommand() {
        return new CouncilUpdateCommand(memberIds.stream().distinct().toList(), role, councilDepartment);
    }
}
