package kr.ac.kookmin.stream.common;

public final class FileUrlUtil {

    private FileUrlUtil() {}

    /** publicBaseUrl 끝에 슬래시가 있어도 이어붙인 URL에 슬래시가 중복되지 않게 한다. */
    public static String buildPublicUrl(String publicBaseUrl, String fileKey) {
        String trimmedBaseUrl = publicBaseUrl.endsWith("/")
            ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1)
            : publicBaseUrl;
        return trimmedBaseUrl + "/" + fileKey;
    }
}
