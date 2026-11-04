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

    /** 약관 하나의 동의 여부. 동의 기록이 없으면 동의하지 않은 것으로 본다. */
    boolean hasAgreed(Long memberId, TermType termType);

    /** 선택 약관 하나의 동의 여부를 가입과 별개로 바꾼다. 필수 약관은 이 경로로 바꾸지 않는다. */
    void updateAgreement(Long memberId, TermType termType, boolean agreed);
}
