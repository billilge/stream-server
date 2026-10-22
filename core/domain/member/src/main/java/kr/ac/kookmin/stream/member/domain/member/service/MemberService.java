package kr.ac.kookmin.stream.member.domain.member.service;

import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberPhoneNumberUpdateCommand;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberProfileCommand;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberSignUpCommand;

public interface MemberService {
    Member getById(Long id);
    List<Member> findAllByIds(List<Long> ids);
    Map<Long, Member> getMapByIds(List<Long> ids);
    /**
     * 이름 또는 학번에 검색어가 포함된 회원 ID를 조회한다. 앞뒤 공백은 지우고 찾는다.
     * 검색할지 말지는 호출하는 쪽이 정하므로, 공백뿐인 검색어는 넘기지 않는다.
     */
    List<Long> searchIdsByKeyword(String keyword);
    Member updateProfile(Long id, MemberProfileCommand command);
    Member registerOrUpdateByStudentId(MemberProfileCommand command);
    Member signUp(Long memberId, MemberSignUpCommand command);
    boolean isSignedUp(Long memberId);
    void updateFcmToken(Long memberId, String fcmToken);
    void updatePhoneNumber(Long memberId, MemberPhoneNumberUpdateCommand command);
}
