package kr.ac.kookmin.stream.common;

import java.util.Set;

public interface PrincipalProvider {
    Long userId();
    Set<Role> roles();
    Set<CouncilDepartment> councilDepartments();

    // 지금 요청이 들고 온 JWT 원문. 이 사용자 권한으로 다른 서버가 학생 API를 대신 부를 때만 쓴다(AI 서버 중계).
    // 로그·응답에 남기지 않는다
    String accessToken();
}
