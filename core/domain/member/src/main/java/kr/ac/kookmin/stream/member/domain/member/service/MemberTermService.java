package kr.ac.kookmin.stream.member.domain.member.service;

import java.util.Map;
import kr.ac.kookmin.stream.member.domain.member.domain.TermType;

public interface MemberTermService {
    boolean hasAgreedRequiredTerms(Long memberId);
    void agree(Long memberId, Map<TermType, Boolean> termAgreements);
}
