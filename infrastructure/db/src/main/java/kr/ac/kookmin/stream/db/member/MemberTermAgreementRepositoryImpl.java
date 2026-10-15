package kr.ac.kookmin.stream.db.member;

import java.util.List;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberTermAgreement;
import kr.ac.kookmin.stream.member.domain.member.repository.MemberTermAgreementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberTermAgreementRepositoryImpl implements MemberTermAgreementRepository {

    private final MemberTermAgreementJpaRepository memberTermAgreementJpaRepository;

    @Override
    public List<MemberTermAgreement> findAllByMemberId(Long memberId) {
        return memberTermAgreementJpaRepository.findAllByMemberId(memberId).stream()
            .map(MemberTermAgreementJpaEntity::toDomain)
            .toList();
    }

    @Override
    public void saveAll(List<MemberTermAgreement> agreements) {
        memberTermAgreementJpaRepository.saveAll(agreements.stream()
            .map(MemberTermAgreementJpaEntity::from)
            .toList());
    }
}
