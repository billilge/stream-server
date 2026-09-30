package kr.ac.kookmin.stream.api.common;

// 웹 응답 전반에서 쓰는 공통 URL 상수 모음.
public final class WebConstants {

    // 파일 공개 URL 조립 기준 주소. 값이 바뀔 일이 거의 없어 설정값이 아닌 상수로 둔다.
    public static final String STORAGE_BASE_URL = "https://static.billilge.site/kmusw-stream";

    public static String buildStorageUrl(String fileKey) {
        return STORAGE_BASE_URL + "/" + fileKey;
    }

    private WebConstants() {}
}
