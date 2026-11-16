package kr.ac.kookmin.stream.member.domain.member.domain;

// admin_config_values에 저장되는 회원 관련 키
public final class MemberConfigKeys {

    // 소속 검사 없이 가입·로그인을 허용할 학번 목록(콤마 구분)
    public static final String DEPARTMENT_BYPASS_STUDENT_IDS = "MEMBER_DEPARTMENT_BYPASS_STUDENT_IDS";

    private MemberConfigKeys() {}
}
