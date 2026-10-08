package kr.ac.kookmin.stream.db.member;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.db.common.ConstraintViolationUtil;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberErrorCode;
import kr.ac.kookmin.stream.member.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private static final String PHONE_NUMBER_UNIQUE_CONSTRAINT = "uk_members_active_phone_number";

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Optional<Member> findById(Long id) {
        return memberJpaRepository.findByIdAndIsDeletedFalse(id).map(MemberJpaEntity::toDomain);
    }

    @Override
    public Optional<Member> findByStudentId(String studentId) {
        return memberJpaRepository.findByStudentIdAndIsDeletedFalse(studentId).map(MemberJpaEntity::toDomain);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return memberJpaRepository.existsByPhoneNumberAndIsDeletedFalse(phoneNumber);
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

    @Override
    public Member save(Member member) {
        try {
            // 동시 가입으로 중복 검사를 함께 통과한 경우 유니크 인덱스 위반을 이 자리에서 잡으려면 바로 반영해야 한다
            return memberJpaRepository.saveAndFlush(MemberJpaEntity.from(member)).toDomain();
        } catch (DataIntegrityViolationException e) {
            if (ConstraintViolationUtil.isViolated(e, PHONE_NUMBER_UNIQUE_CONSTRAINT)) {
                throw new BusinessException(MemberErrorCode.PHONE_NUMBER_ALREADY_EXISTS);
            }
            throw e;
        }
    }
}
