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
    List<Long> searchIdsByKeyword(String keyword);
    Member updateProfile(Long id, MemberProfileCommand command);
    Member registerOrUpdateByStudentId(MemberProfileCommand command);
    Member signUp(Long memberId, MemberSignUpCommand command);
    boolean isSignedUp(Long memberId);
    void updateFcmToken(Long memberId, String fcmToken);
    void updatePhoneNumber(Long memberId, MemberPhoneNumberUpdateCommand command);
}
