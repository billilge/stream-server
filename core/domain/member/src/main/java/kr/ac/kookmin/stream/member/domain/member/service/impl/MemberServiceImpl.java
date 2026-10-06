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
import kr.ac.kookmin.stream.member.domain.member.domain.MemberSignUpCommand;
import kr.ac.kookmin.stream.member.domain.member.repository.MemberRepository;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.member.domain.member.service.MemberTermService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberTermService memberTermService;

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

    // 전화번호 저장과 약관 동의를 한 트랜잭션으로 묶는다. 필수 약관에 동의하지 않으면 전화번호 저장도 롤백된다
    @Override
    @Transactional
    public Member signUp(Long memberId, MemberSignUpCommand command) {
        Member member = getById(memberId);
        validateNotSignedUp(member);
        validatePhoneNumberNotDuplicated(member, command.phoneNumber());

        member.registerPhoneNumber(command.phoneNumber());
        Member saved = memberRepository.save(member);
        memberTermService.agree(memberId, command.termAgreements());
        return saved;
    }

    @Override
    public boolean isSignedUp(Long memberId) {
        return isSignedUp(getById(memberId));
    }

    // 전화번호와 필수 약관 동의가 모두 있어야 가입을 마친 것으로 본다
    private boolean isSignedUp(Member member) {
        return member.hasPhoneNumber() && memberTermService.hasAgreedRequiredTerms(member.getId());
    }

    private void validateNotSignedUp(Member member) {
        if (isSignedUp(member)) {
            throw new BusinessException(MemberErrorCode.ALREADY_SIGNED_UP);
        }
    }

    // 필수 약관이 늘어 다시 가입하는 회원은 자기 번호를 그대로 보낼 수 있으므로 같은 번호면 중복 검사를 건너뛴다
    private void validatePhoneNumberNotDuplicated(Member member, String phoneNumber) {
        if (phoneNumber.equals(member.getPhoneNumber())) {
            return;
        }
        if (memberRepository.existsByPhoneNumber(phoneNumber)) {
            throw new BusinessException(MemberErrorCode.PHONE_NUMBER_ALREADY_EXISTS);
        }
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
        boolean changed = member.updateProfile(command.name(), Department.fromMajor(command.major()), command.academicStatus());
        if (!changed) {
            return member;
        }
        return memberRepository.save(member);
    }
}
