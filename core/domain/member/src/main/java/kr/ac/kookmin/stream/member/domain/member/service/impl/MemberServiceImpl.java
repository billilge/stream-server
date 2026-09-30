package kr.ac.kookmin.stream.member.domain.member.service.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.member.domain.member.domain.Department;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberErrorCode;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberProfileCommand;
import kr.ac.kookmin.stream.member.domain.member.repository.MemberRepository;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public Member getById(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    @Override
    public List<Member> findAllByIds(List<Long> ids) {
        return memberRepository.findAllByIds(ids);
    }

    @Override
    public Map<Long, Member> getMapByIds(List<Long> ids) {
        return findAllByIds(ids)
                .stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));
    }

    @Override
    public List<Long> searchIdsByKeyword(String keyword) {
        return memberRepository.searchIdsByKeyword(keyword);
    }

    @Override
    @Transactional
    public Member updateProfile(Long id, MemberProfileCommand command) {
        Member member = getById(id);
        member.updateProfile(command.name(), Department.fromMajor(command.major()), command.academicStatus());
        return memberRepository.save(member);
    }

    @Override
    @Transactional
    public Member registerOrUpdateByStudentId(MemberProfileCommand command) {
        Department department = Department.fromMajor(command.major());
        Member member = memberRepository.findByStudentId(command.studentId())
            .map(existing -> {
                existing.updateProfile(command.name(), department, command.academicStatus());
                return existing;
            })
            .orElseGet(() -> Member.create(command.studentId(), command.name(), department, command.academicStatus()));
        return memberRepository.save(member);
    }
}
