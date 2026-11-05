package kr.ac.kookmin.stream.member.domain.member.service;

import java.util.List;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.common.Role;
import kr.ac.kookmin.stream.member.domain.member.domain.CouncilAssignCommand;
import kr.ac.kookmin.stream.member.domain.member.domain.CouncilUpdateCommand;
import kr.ac.kookmin.stream.member.domain.member.domain.Department;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;

/** 운영진 콘솔 전용 회원 조회·관리자 관리. 학생 앱이 쓰는 {@link MemberService}와 분리한다(coding-style.md 2-7절). */
public interface AdminMemberService {

    /**
     * @param keyword     이름·학번·이메일 부분 일치 검색어. null이거나 공백이면 거르지 않는다
     * @param department  학부. null이면 거르지 않는다
     * @param role        역할. null이면 거르지 않는다
     * @param councilOnly true면 관리자 관리 명단(운영진이거나 학생회 부서가 있는 회원)만 조회한다
     */
    PageResult<Member> search(
        String keyword, Department department, Role role, boolean councilOnly, PageOffset pageOffset);

    /**
     * 회원들을 같은 학생회 부서의 운영진으로 지정한다. 이미 명단에 있는 회원이 섞여 있으면 부서를 조용히
     * 덮어쓰지 않도록 {@code ALREADY_COUNCIL_MEMBER}로 아무도 바꾸지 않는다.
     */
    void assignCouncil(CouncilAssignCommand command);

    /**
     * 회원들의 권한을 바꾸고, 부서를 받았으면 부서도 바꾼다. 부서를 보내지 않으면 각자의 지금 부서를 그대로 둔다.
     */
    void updateCouncil(CouncilUpdateCommand command);

    /** 회원들을 관리자 관리 명단에서 뺀다. 권한을 학생으로 되돌리고 학생회 부서를 비운다. */
    void releaseCouncil(List<Long> memberIds);
}
