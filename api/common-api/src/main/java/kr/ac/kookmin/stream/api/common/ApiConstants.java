package kr.ac.kookmin.stream.api.common;

// API 응답 전반에서 쓰는 공통 상수 모음.
public final class ApiConstants {

    // member가 null이면 탈퇴(소프트 삭제)한 회원의 과거 요청이다 — 이름 대신 쓰는 표시용 placeholder.
    public static final String WITHDRAWN_MEMBER_LABEL = "(탈퇴한 회원)";

    // 파일 공개 URL 조립 기준 주소. prod·dev가 같은 도메인을 보고 있어 상수로 둔다 — 설정값으로 빼면
    // 한쪽에서 누락됐을 때 모든 이미지 URL이 통째로 깨진다.
    // 뒤에 버킷 이름을 붙이지 않는다. R2 커스텀 도메인은 버킷 루트를 도메인 루트에 서브하고,
    // 업로드되는 키도 files/{uuid}.{ext}라 경로에 버킷 이름이 들어갈 자리가 없다.
    public static final String STORAGE_BASE_URL = "https://static.kmusw.org";

    private ApiConstants() {}
}
