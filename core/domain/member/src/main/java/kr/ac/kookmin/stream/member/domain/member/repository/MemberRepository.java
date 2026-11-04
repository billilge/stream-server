package kr.ac.kookmin.stream.member.domain.member.repository;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;

public interface MemberRepository {
    Optional<Member> findById(Long id);
    Optional<Member> findByStudentId(String studentId);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    List<Member> findAllByIds(List<Long> ids);
    List<Long> searchIdsByKeyword(String keyword);
    // 학생회 구성원(학생회 부서가 있는 운영진). 이름, 같은 이름이면 학번 오름차순
    List<Member> findAllCouncilMembers();
    Member save(Member member);
    void clearFcmTokenOfOthers(Long memberId, String fcmToken);
    // 소프트 삭제한다. 이미 탈퇴했거나 없는 회원이면 아무것도 하지 않는다
    void delete(Long id);
}
