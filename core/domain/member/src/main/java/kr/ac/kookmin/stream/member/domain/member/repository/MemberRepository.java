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
    Member save(Member member);
}
