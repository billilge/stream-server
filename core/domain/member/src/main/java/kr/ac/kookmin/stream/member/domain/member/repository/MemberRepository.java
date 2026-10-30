package kr.ac.kookmin.stream.member.domain.member.repository;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;

public interface MemberRepository {
    Optional<Member> findById(Long id);
    Optional<Member> findByStudentId(String studentId);
    boolean existsByPhoneNumber(String phoneNumber);
    List<Member> findAllByIds(List<Long> ids);
    List<Long> searchIdsByKeyword(String keyword);
    // 학생회 구성원(학생회 부서가 있는 회원). 이름, 같은 이름이면 학번 오름차순
    List<Member> findAllCouncilMembers();
    Member save(Member member);
    void clearFcmTokenOfOthers(Long memberId, String fcmToken);
}
