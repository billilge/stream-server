package kr.ac.kookmin.stream.member.domain.member.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import kr.ac.kookmin.stream.member.domain.member.domain.TermType;

public interface MemberTermService {
    boolean hasAgreedRequiredTerms(Long memberId);
    // 주어진 회원 중 필수 약관에 모두 동의한 회원 ID. 기준은 hasAgreedRequiredTerms와 같다
    Set<Long> findIdsAgreedRequiredTerms(List<Long> memberIds);
    void agree(Long memberId, Map<TermType, Boolean> termAgreements);
}
