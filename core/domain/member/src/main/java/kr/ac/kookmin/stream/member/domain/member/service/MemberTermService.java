package kr.ac.kookmin.stream.member.domain.member.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import kr.ac.kookmin.stream.member.domain.member.domain.TermType;

public interface MemberTermService {
    boolean hasAgreedRequiredTerms(Long memberId);

    /** 필수 약관에 모두 동의한 회원의 id. 목록에서 회원마다 조회하지 않으려고 한 번에 가져온다. */
    Set<Long> findMemberIdsAgreedRequiredTerms(List<Long> memberIds);
    void agree(Long memberId, Map<TermType, Boolean> termAgreements);
}
