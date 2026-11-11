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
    // 학생회 구성원(학생회 부서가 있는 운영진). 이름, 같은 이름이면 학번 오름차순
    List<Member> findAllCouncilMembers();

    /**
     * 운영진 콘솔용 회원 목록. 이름순으로 페이지 단위로 조회한다.
     *
     * @param keyword     이름·학번·이메일 부분 일치 검색어. null이거나 공백이면 거르지 않는다
     * @param department  학부. null이면 거르지 않는다
     * @param role        역할. null이면 거르지 않는다
     * @param councilOnly true면 운영진이거나 학생회 부서가 있는 회원만 거른다
     */
    PageResult<Member> search(
        String keyword, Department department, Role role, boolean councilOnly, PageOffset pageOffset);

    // 회장단 운영진 수. 마지막 회장단이 사라지는 변경을 막을 때 쓴다
    long countPresidencyAdmins();

    Member save(Member member);
    void clearFcmTokenOfOthers(Long memberId, String fcmToken);
    // 소프트 삭제한다. 이미 탈퇴했거나 없는 회원이면 아무것도 하지 않는다
    void delete(Long id);
}
