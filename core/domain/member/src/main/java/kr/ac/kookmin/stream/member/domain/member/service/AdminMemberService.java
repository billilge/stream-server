package kr.ac.kookmin.stream.member.domain.member.service;

import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.common.Role;
import kr.ac.kookmin.stream.member.domain.member.domain.Department;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;

/** 운영진 콘솔 전용 회원 조회. 학생 앱이 쓰는 {@link MemberService}와 분리한다(coding-style.md 2-7절). */
public interface AdminMemberService {

    /**
     * @param keyword    이름·학번·이메일 부분 일치 검색어. null이거나 공백이면 거르지 않는다
     * @param department 학부. null이면 거르지 않는다
     * @param role       역할. null이면 거르지 않는다
     */
    PageResult<Member> search(String keyword, Department department, Role role, PageOffset pageOffset);
}
