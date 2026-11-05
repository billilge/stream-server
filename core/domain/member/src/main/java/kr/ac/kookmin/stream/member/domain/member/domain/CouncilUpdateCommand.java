package kr.ac.kookmin.stream.member.domain.member.domain;

import java.util.List;
import kr.ac.kookmin.stream.common.CouncilDepartment;
import kr.ac.kookmin.stream.common.Role;

/**
 * 관리자 권한·학생회 부서를 바꾼다. 한 명을 고치는 화면과 여러 명의 권한을 한 번에 바꾸는 화면이 같이 쓴다.
 *
 * @param councilDepartment 바꿀 부서. {@code null}이면 각자의 지금 부서를 그대로 둔다 — 권한만 일괄로
 *                          바꾸는 화면은 부서를 보내지 않는다
 */
public record CouncilUpdateCommand(List<Long> memberIds, Role role, CouncilDepartment councilDepartment) {
}
