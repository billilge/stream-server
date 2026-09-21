package kr.ac.kookmin.stream.member.domain.member.repository;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;

public interface MemberRepository {
    Optional<Member> findById(Long id);
    List<Member> findAllByIds(List<Long> ids);
    List<Long> searchIdsByKeyword(String keyword);
}
