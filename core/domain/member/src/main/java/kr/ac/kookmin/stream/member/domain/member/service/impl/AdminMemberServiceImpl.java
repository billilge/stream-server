package kr.ac.kookmin.stream.member.domain.member.service.impl;

import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.common.Role;
import kr.ac.kookmin.stream.member.domain.member.domain.Department;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.repository.MemberRepository;
import kr.ac.kookmin.stream.member.domain.member.service.AdminMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class AdminMemberServiceImpl implements AdminMemberService {

    private final MemberRepository memberRepository;

    @Override
    public PageResult<Member> search(String keyword, Department department, Role role, PageOffset pageOffset) {
        return memberRepository.search(keyword, department, role, pageOffset);
    }
}
