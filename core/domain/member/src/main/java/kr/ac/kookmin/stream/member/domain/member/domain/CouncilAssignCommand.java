package kr.ac.kookmin.stream.member.domain.member.domain;

import java.util.List;
import kr.ac.kookmin.stream.common.CouncilDepartment;

/**
 * 여러 회원을 같은 학생회 부서의 운영진으로 지정한다. 부서는 한 번에 하나만 받는다 — 운영진 화면이 선택한 회원
 * 전체에 같은 부서를 주는 구조다.
 */
public record CouncilAssignCommand(List<Long> memberIds, CouncilDepartment councilDepartment) {
}
