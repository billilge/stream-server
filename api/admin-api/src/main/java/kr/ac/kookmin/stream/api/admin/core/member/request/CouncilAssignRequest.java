package kr.ac.kookmin.stream.api.admin.core.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import kr.ac.kookmin.stream.common.CouncilDepartment;
import kr.ac.kookmin.stream.member.domain.member.domain.CouncilAssignCommand;

/**
 * 회원들을 운영진으로 지정한다. 권한은 받지 않는다 — 지정은 관리자로 만드는 것뿐이고, 학생으로 되돌리는 것은
 * 권한 변경과 해제가 맡는다. 부서는 하나만 받아 선택한 회원 전체에 같은 값을 준다.
 */
public record CouncilAssignRequest(

    @NotEmpty(message = "관리자로 지정할 회원을 한 명 이상 선택해 주세요.")
    @Schema(description = "관리자로 지정할 회원 식별자", example = "[1, 2]")
    List<@NotNull(message = "회원 식별자는 비워 둘 수 없습니다.") Long> memberIds,

    @NotNull(message = "학생회 부서를 선택해 주세요.")
    @Schema(description = "부여할 학생회 부서", example = "GENERAL_AFFAIRS")
    CouncilDepartment councilDepartment
) {

    // 중복을 걷어야 조회한 회원 수와 요청한 수를 견줘 없는 회원을 가려낼 수 있다
    public CouncilAssignCommand toCommand() {
        return new CouncilAssignCommand(memberIds.stream().distinct().toList(), councilDepartment);
    }
}
