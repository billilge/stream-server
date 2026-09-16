package kr.ac.kookmin.stream.member.domain.member.service;

import java.util.List;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;

public interface MemberService {
    Member getById(Long id);
    List<Member> findAllByIds(List<Long> ids);
    List<Long> searchIdsByKeyword(String keyword);
}
