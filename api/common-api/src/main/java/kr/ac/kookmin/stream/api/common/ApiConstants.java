package kr.ac.kookmin.stream.api.common;

// API 응답 전반에서 쓰는 공통 상수 모음.
public final class ApiConstants {

    // member가 null이면 탈퇴(소프트 삭제)한 회원의 과거 요청이다 — 이름 대신 쓰는 표시용 placeholder.
    public static final String WITHDRAWN_MEMBER_LABEL = "(탈퇴한 회원)";

    private ApiConstants() {}
}
