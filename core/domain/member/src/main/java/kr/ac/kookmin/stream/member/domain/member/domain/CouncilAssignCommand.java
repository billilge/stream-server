package kr.ac.kookmin.stream.member.domain.member.domain;

import java.util.List;
import kr.ac.kookmin.stream.common.CouncilDepartment;

/** 여러 회원을 같은 학생회 부서의 운영진으로 지정한다. */
public record CouncilAssignCommand(List<Long> memberIds, CouncilDepartment councilDepartment) {
}
