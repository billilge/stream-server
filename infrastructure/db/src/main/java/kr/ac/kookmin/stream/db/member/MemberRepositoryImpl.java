package kr.ac.kookmin.stream.db.member;

import java.util.Optional;
import kr.ac.kookmin.stream.member.Member;
import kr.ac.kookmin.stream.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Optional<Member> findById(Long id) {
        return memberJpaRepository.findByIdAndDeletedAtIsNull(id).map(MemberJpaEntity::toDomain);
    }
}
