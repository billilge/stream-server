package kr.ac.kookmin.stream.member.domain.member.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberProfileCommand;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberSignUpCommand;

public interface MemberService {
    Member getById(Long id);
    List<Member> findAllByIds(List<Long> ids);
    Map<Long, Member> getMapByIds(List<Long> ids);
    List<Long> searchIdsByKeyword(String keyword);

    /** 회비 상태와 검색어로 회원을 페이지 단위로 조회한다. 관리자 납부자 목록이 회원 기준이라 필요하다. */
    PageResult<Member> searchByFeeStatus(String feeStatus, String keyword, PageOffset pageOffset);

    /** 회원가입(전화번호 + 필수 약관 동의)을 마친 회원의 id. 목록의 Stream 가입 상태에 쓴다. */
    Set<Long> findSignedUpMemberIds(List<Long> memberIds);
    Member updateProfile(Long id, MemberProfileCommand command);
    Member registerOrUpdateByStudentId(MemberProfileCommand command);
    Member signUp(Long memberId, MemberSignUpCommand command);
    boolean isSignedUp(Long memberId);
    void updateFcmToken(Long memberId, String fcmToken);
}
