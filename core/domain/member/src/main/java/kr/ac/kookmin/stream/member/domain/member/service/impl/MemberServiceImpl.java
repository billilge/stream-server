package kr.ac.kookmin.stream.member.domain.member.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        return applyProfile(getById(id), command);
    }

    @Override
    @Transactional
    public Member registerOrUpdateByStudentId(MemberProfileCommand command) {
        Optional<Member> found = memberRepository.findByStudentId(command.studentId());
        if (found.isEmpty()) {
            return register(command);
        }
        return applyProfile(found.get(), command);
    }

    private Member register(MemberProfileCommand command) {
        Member member = Member.create(
            command.studentId(),
            command.name(),
            Department.fromMajor(command.major()),
            command.academicStatus()
        );
        return memberRepository.save(member);
    }

    private Member applyProfile(Member member, MemberProfileCommand command) {
        member.updateProfile(command.name(), Department.fromMajor(command.major()), command.academicStatus());
        return memberRepository.save(member);
    }
}
