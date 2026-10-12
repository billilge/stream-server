package kr.ac.kookmin.stream.api.common;

// API 응답 전반에서 쓰는 공통 상수 모음.
public final class ApiConstants {

    // member가 null이면 탈퇴(소프트 삭제)한 회원의 과거 요청이다 — 이름 대신 쓰는 표시용 placeholder.
    public static final String WITHDRAWN_MEMBER_LABEL = "(탈퇴한 회원)";

    // 파일 공개 URL 조립 기준 주소. 값이 바뀔 일이 거의 없어 설정값이 아닌 상수로 둔다.
    public static final String STORAGE_BASE_URL = "https://static.billilge.site/kmusw-stream";

    private ApiConstants() {}
}
