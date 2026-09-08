package kr.ac.kookmin.stream.member.domain.member.repository;

import java.util.Optional;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;

public interface MemberRepository {
    Optional<Member> findById(Long id);
}
