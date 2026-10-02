package kr.ac.kookmin.stream.member.domain.member.service.impl;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberTermAgreement;
import kr.ac.kookmin.stream.member.domain.member.domain.TermType;
import kr.ac.kookmin.stream.member.domain.member.repository.MemberTermAgreementRepository;
import kr.ac.kookmin.stream.member.domain.member.service.MemberTermService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class MemberTermServiceImpl implements MemberTermService {

    private final MemberTermAgreementRepository memberTermAgreementRepository;

    @Override
    public boolean hasAgreedRequiredTerms(Long memberId) {
        Set<TermType> agreedTermTypes = memberTermAgreementRepository.findAllByMemberId(memberId).stream()
            .filter(MemberTermAgreement::isAgreed)
            .map(MemberTermAgreement::getTermType)
            .collect(Collectors.toSet());
        return Arrays.stream(TermType.values())
            .filter(TermType::required)
            .allMatch(agreedTermTypes::contains);
    }
}
