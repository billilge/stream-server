package kr.ac.kookmin.stream.member.domain.member.repository;

import java.util.List;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberTermAgreement;

public interface MemberTermAgreementRepository {
    List<MemberTermAgreement> findAllByMemberId(Long memberId);
    List<MemberTermAgreement> findAllByMemberIdIn(List<Long> memberIds);
    void saveAll(List<MemberTermAgreement> agreements);
}
