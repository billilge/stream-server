package kr.ac.kookmin.stream.member.domain.member.service.impl;

import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberErrorCode;
import kr.ac.kookmin.stream.member.domain.member.repository.MemberRepository;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public List<Long> searchIdsByKeyword(String keyword) {
        return memberRepository.searchIdsByKeyword(keyword);
    }
}
