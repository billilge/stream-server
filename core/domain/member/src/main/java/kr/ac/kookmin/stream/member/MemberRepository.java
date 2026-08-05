package kr.ac.kookmin.stream.member;

import java.util.Optional;

public interface MemberRepository {
    Optional<Member> findById(Long id);
}
