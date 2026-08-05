package kr.ac.kookmin.stream.member.internal;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.member.Member;
import kr.ac.kookmin.stream.member.MemberErrorCode;
import kr.ac.kookmin.stream.member.MemberRepository;
import kr.ac.kookmin.stream.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class MemberInternalService implements MemberService {

    private final MemberRepository memberRepository;

    public Member getById(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
