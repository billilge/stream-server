package kr.ac.kookmin.stream.member.domain.member.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberPhoneNumberUpdateCommand;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberProfileCommand;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberSignUpCommand;

public interface MemberService {
    Member getById(Long id);
    List<Member> findAllByIds(List<Long> ids);
    Map<Long, Member> getMapByIds(List<Long> ids);
    /**
     * 이름 또는 학번에 검색어가 포함된 회원 ID를 조회한다. 앞뒤 공백은 지우고 {@code %}·{@code _}는 글자 그대로 찾는다.
     * 검색할지 말지는 호출하는 쪽이 정하므로, 검색어가 null이거나 공백뿐이면 일치하는 회원이 없어 빈 목록이다.
     */
    List<Long> searchIdsByKeyword(String keyword);
    // 학생회 구성원(학생회 부서가 있는 회원). 이름, 같은 이름이면 학번 오름차순
    List<Member> findAllCouncilMembers();
    Member updateProfile(Long id, MemberProfileCommand command);
    Member registerOrUpdateByStudentId(MemberProfileCommand command);
    Member signUp(Long memberId, MemberSignUpCommand command);
    boolean isSignedUp(Long memberId);
    /**
     * 주어진 회원 중 가입(전화번호 등록 + 필수 약관 동의)을 마친 회원 ID. 기준은 {@link #isSignedUp}과 같고 탈퇴한 회원은 빠진다.
     * 목록에서 회원마다 isSignedUp을 부르지 않도록 한 번에 조회한다.
     */
    Set<Long> findSignedUpIds(List<Long> memberIds);
    void updateFcmToken(Long memberId, String fcmToken);
    void updatePhoneNumber(Long memberId, MemberPhoneNumberUpdateCommand command);
}
