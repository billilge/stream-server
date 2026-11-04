package kr.ac.kookmin.stream.member.domain.member.repository;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.common.Role;
import kr.ac.kookmin.stream.member.domain.member.domain.Department;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;

public interface MemberRepository {
    Optional<Member> findById(Long id);
    Optional<Member> findByStudentId(String studentId);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    List<Member> findAllByIds(List<Long> ids);
    List<Long> searchIdsByKeyword(String keyword);

    /**
     * 운영진 콘솔용 회원 목록. 이름순으로 페이지 단위로 조회한다.
     *
     * @param keyword    이름·학번·이메일 부분 일치 검색어. null이거나 공백이면 거르지 않는다
     * @param department 학부. null이면 거르지 않는다
     * @param role       역할. null이면 거르지 않는다
     */
    PageResult<Member> search(String keyword, Department department, Role role, PageOffset pageOffset);

    Member save(Member member);
    void clearFcmTokenOfOthers(Long memberId, String fcmToken);
}
