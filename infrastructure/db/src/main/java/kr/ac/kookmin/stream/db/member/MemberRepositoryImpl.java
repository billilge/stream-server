package kr.ac.kookmin.stream.db.member;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Optional<Member> findById(Long id) {
        return memberJpaRepository.findByIdAndIsDeletedFalse(id).map(MemberJpaEntity::toDomain);
    }

    @Override
    public List<Member> findAllByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return memberJpaRepository.findAllByIdInAndIsDeletedFalse(ids).stream()
            .map(MemberJpaEntity::toDomain)
            .toList();
    }

    @Override
    public List<Long> searchIdsByKeyword(String keyword) {
        return memberJpaRepository.searchIdsByKeyword(keyword);
    }
}
