package kr.ac.kookmin.stream.api.common;

import java.util.Map;
import kr.ac.kookmin.stream.file.domain.File;

public final class StorageUrlBuilder {

    public static String build(String fileKey) {
        return fileKey == null ? null : ApiConstants.STORAGE_BASE_URL + "/" + fileKey;
    }

    /**
     * fileId가 null이거나 filesById에 없으면(삭제된 파일) null을 돌려준다.
     */
    public static String build(Long fileId, Map<Long, File> filesById) {
        if (fileId == null) {
            return null;
        }
        File file = filesById.get(fileId);
        return file == null ? null : build(file.getFileKey());
    }

    private StorageUrlBuilder() {}
}
