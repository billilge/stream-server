package kr.ac.kookmin.stream.member.domain.member.domain;

import java.util.List;
import kr.ac.kookmin.stream.common.CouncilDepartment;
import kr.ac.kookmin.stream.common.Role;

/**
 * 관리자 권한·학생회 부서를 바꾼다.
 *
 * @param councilDepartment 바꿀 부서. {@code null}이면 각자의 지금 부서를 그대로 둔다
 */
public record CouncilUpdateCommand(List<Long> memberIds, Role role, CouncilDepartment councilDepartment) {
}
