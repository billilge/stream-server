package kr.ac.kookmin.stream.member.domain.member.service;

import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberProfileCommand;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberSignUpCommand;

public interface MemberService {

    /**
     * 회비 납부 상태를 반영한다. 실제 상태는 welfare가 들고 있고 여기 값은 사본이므로,
     * 호출부(UseCase)가 두 값을 한 트랜잭션에서 함께 바꾼다.
     */
    Member changeFeeStatus(Long memberId, String feeStatus);
    Member getById(Long id);
    List<Member> findAllByIds(List<Long> ids);
    Map<Long, Member> getMapByIds(List<Long> ids);
    List<Long> searchIdsByKeyword(String keyword);
    Member updateProfile(Long id, MemberProfileCommand command);
    Member registerOrUpdateByStudentId(MemberProfileCommand command);
    Member signUp(Long memberId, MemberSignUpCommand command);
    boolean isSignedUp(Long memberId);
    void updateFcmToken(Long memberId, String fcmToken);
}
