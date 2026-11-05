package kr.ac.kookmin.stream.member.domain.member.service.impl;

import java.util.List;
import java.util.function.Consumer;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.common.Role;
import kr.ac.kookmin.stream.member.domain.member.domain.CouncilAssignCommand;
import kr.ac.kookmin.stream.member.domain.member.domain.CouncilUpdateCommand;
import kr.ac.kookmin.stream.member.domain.member.domain.Department;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberErrorCode;
import kr.ac.kookmin.stream.member.domain.member.repository.MemberRepository;
import kr.ac.kookmin.stream.member.domain.member.service.AdminMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class AdminMemberServiceImpl implements AdminMemberService {

    private final MemberRepository memberRepository;

    @Override
    public PageResult<Member> search(
        String keyword, Department department, Role role, boolean councilOnly, PageOffset pageOffset
    ) {
        return memberRepository.search(keyword, department, role, councilOnly, pageOffset);
    }

    @Override
    @Transactional
    public void assignCouncil(CouncilAssignCommand command) {
        List<Member> members = getAllByIds(command.memberIds());
        if (members.stream().anyMatch(Member::isCouncilRegistered)) {
            throw new BusinessException(MemberErrorCode.ALREADY_COUNCIL_MEMBER);
        }

        apply(members, member -> member.assignCouncil(command.councilDepartment()));
    }

    @Override
    @Transactional
    public void updateCouncil(CouncilUpdateCommand command) {
        List<Member> members = getAllByIds(command.memberIds());
        // 명단에 없는 회원을 이 길로 운영진으로 만들 수 없게 막는다. 지정은 납부자인지 확인하는 assignCouncil만 한다
        if (!members.stream().allMatch(Member::isCouncilRegistered)) {
            throw new BusinessException(MemberErrorCode.NOT_COUNCIL_MEMBER);
        }

        apply(members, member -> member.changeCouncil(command.role(), command.councilDepartment()));
    }

    @Override
    @Transactional
    public void releaseCouncil(List<Long> memberIds) {
        apply(getAllByIds(memberIds), Member::releaseCouncil);
    }

    /**
     * 바꾸고 저장한다. 바꾼 결과 회장단 운영진이 한 명도 남지 않으면 거부한다 — 회장단만 부를 수 있는 API라
     * 아무도 관리자를 바꿀 수 없게 된다. 세고 나서 바꾸는 구조라 두 요청이 겹치면 둘 다 통과할 수 있는데,
     * 회장단 규모와 사용 빈도를 보면 겹칠 일이 없다고 보고 락을 걸지 않는다.
     */
    private void apply(List<Member> members, Consumer<Member> change) {
        long presidencyBefore = members.stream().filter(Member::isPresidencyAdmin).count();
        members.forEach(change);
        long presidencyAfter = members.stream().filter(Member::isPresidencyAdmin).count();

        if (memberRepository.countPresidencyAdmins() - presidencyBefore + presidencyAfter <= 0) {
            throw new BusinessException(MemberErrorCode.LAST_PRESIDENCY_ADMIN_REQUIRED);
        }
        members.forEach(memberRepository::save);
    }

    // 없는 회원이 하나라도 섞여 있으면 아무도 바꾸지 않는다. memberIds는 중복이 걷힌 목록이다
    private List<Member> getAllByIds(List<Long> memberIds) {
        List<Member> members = memberRepository.findAllByIds(memberIds);
        if (members.size() != memberIds.size()) {
            throw new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND);
        }
        return members;
    }
}
