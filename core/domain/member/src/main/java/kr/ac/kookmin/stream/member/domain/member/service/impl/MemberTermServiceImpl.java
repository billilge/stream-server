package kr.ac.kookmin.stream.member.domain.member.service.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberErrorCode;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberTermAgreement;
import kr.ac.kookmin.stream.member.domain.member.domain.TermType;
import kr.ac.kookmin.stream.member.domain.member.repository.MemberTermAgreementRepository;
import kr.ac.kookmin.stream.member.domain.member.service.MemberTermService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // 요청에 없는 약관은 동의하지 않은 것으로 보고 기록을 남기지 않는다
    @Override
    @Transactional
    public void agree(Long memberId, Map<TermType, Boolean> termAgreements) {
        validateRequiredTermsAgreed(termAgreements);

        Map<TermType, MemberTermAgreement> existingAgreements = memberTermAgreementRepository.findAllByMemberId(memberId)
            .stream()
            .collect(Collectors.toMap(MemberTermAgreement::getTermType, Function.identity()));
        LocalDateTime now = LocalDateTime.now();
        List<MemberTermAgreement> agreements = termAgreements.entrySet().stream()
            .map(entry -> toAgreement(memberId, entry.getKey(), entry.getValue(), existingAgreements, now))
            .toList();
        memberTermAgreementRepository.saveAll(agreements);
    }

    private void validateRequiredTermsAgreed(Map<TermType, Boolean> termAgreements) {
        boolean requiredTermsAgreed = Arrays.stream(TermType.values())
            .filter(TermType::required)
            .allMatch(termType -> Boolean.TRUE.equals(termAgreements.get(termType)));
        if (!requiredTermsAgreed) {
            throw new BusinessException(MemberErrorCode.REQUIRED_TERMS_NOT_AGREED);
        }
    }

    private MemberTermAgreement toAgreement(
        Long memberId,
        TermType termType,
        boolean agreed,
        Map<TermType, MemberTermAgreement> existingAgreements,
        LocalDateTime now
    ) {
        MemberTermAgreement existing = existingAgreements.get(termType);
        if (existing == null) {
            return MemberTermAgreement.create(memberId, termType, agreed, now);
        }
        existing.update(agreed, now);
        return existing;
    }
}
