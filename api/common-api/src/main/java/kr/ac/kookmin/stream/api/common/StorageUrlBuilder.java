package kr.ac.kookmin.stream.api.common;

public final class StorageUrlBuilder {

    public static String build(String fileKey) {
        return ApiConstants.STORAGE_BASE_URL + "/" + fileKey;
    }

    private StorageUrlBuilder() {}
}
