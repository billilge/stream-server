package kr.ac.kookmin.stream.member.domain.member.repository;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;

public interface MemberRepository {
    Optional<Member> findById(Long id);
    Optional<Member> findByStudentId(String studentId);
    boolean existsByPhoneNumber(String phoneNumber);
    List<Member> findAllByIds(List<Long> ids);
    List<Long> searchIdsByKeyword(String keyword);

    // feeStatus·keyword가 null이면 그 조건은 빼고 전체를 대상으로 한다
    PageResult<Member> searchByFeeStatus(String feeStatus, String keyword, PageOffset pageOffset);
    Member save(Member member);
    void clearFcmTokenOfOthers(Long memberId, String fcmToken);
}
