package kr.ac.kookmin.stream.file.service.impl;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.file.domain.FileCategory;
import kr.ac.kookmin.stream.file.domain.FileErrorCode;

final class FileUploadPolicy {

    // 확장자별로 받는 MIME. 확장자와 MIME을 한 표에 둬서, MIME 없이 확장자만 추가되는 일이 없게 한다.
    // 받은 contentType을 그대로 서명해 저장소가 그 타입으로 내려주므로, 확장자와 맞는 MIME만 받는다
    // image/jpg는 일부 모바일 앱, x-zip-compressed는 Windows, haansofthwp는 한컴 설치 환경이 보내는 값이다
    private static final Map<String, Set<String>> IMAGE_TYPES = Map.of(
        "jpg", Set.of("image/jpeg", "image/jpg"),
        "jpeg", Set.of("image/jpeg", "image/jpg"),
        "png", Set.of("image/png"),
        "webp", Set.of("image/webp")
    );
    private static final Map<String, Set<String>> ATTACHMENT_TYPES = Map.of(
        "pdf", Set.of("application/pdf"),
        "doc", Set.of("application/msword"),
        "docx", Set.of("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
        "hwp", Set.of("application/x-hwp", "application/haansofthwp"),
        "zip", Set.of("application/zip", "application/x-zip-compressed")
    );
    // 빌릴게 물품 아이콘은 SVG로만 받는다
    private static final Map<String, Set<String>> ITEM_ICON_TYPES = Map.of(
        "svg", Set.of("image/svg+xml")
    );

    private static final long IMAGE_MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final long ATTACHMENT_MAX_FILE_SIZE = 20 * 1024 * 1024;

    private static final Map<FileCategory, Map<String, Set<String>>> ALLOWED_TYPES = Map.of(
        FileCategory.TEMP, merge(IMAGE_TYPES, ATTACHMENT_TYPES),
        FileCategory.NOTICE_ATTACHMENT, ATTACHMENT_TYPES,
        FileCategory.NOTICE_IMAGE, IMAGE_TYPES,
        FileCategory.EVENT_IMAGE, IMAGE_TYPES,
        FileCategory.ARCHIVE_IMAGE, IMAGE_TYPES,
        FileCategory.LOCKER_SECTION_PHOTO, IMAGE_TYPES,
        FileCategory.FEE_TRANSCRIPT_IMAGE, IMAGE_TYPES,
        FileCategory.ITEM_IMAGE, ITEM_ICON_TYPES
    );

    private static final Map<FileCategory, Long> MAX_FILE_SIZE = Map.of(
        FileCategory.TEMP, ATTACHMENT_MAX_FILE_SIZE,
        FileCategory.NOTICE_ATTACHMENT, ATTACHMENT_MAX_FILE_SIZE,
        FileCategory.NOTICE_IMAGE, IMAGE_MAX_FILE_SIZE,
        FileCategory.EVENT_IMAGE, IMAGE_MAX_FILE_SIZE,
        FileCategory.ARCHIVE_IMAGE, IMAGE_MAX_FILE_SIZE,
        FileCategory.LOCKER_SECTION_PHOTO, IMAGE_MAX_FILE_SIZE,
        FileCategory.FEE_TRANSCRIPT_IMAGE, IMAGE_MAX_FILE_SIZE,
        FileCategory.ITEM_IMAGE, IMAGE_MAX_FILE_SIZE
    );

    private FileUploadPolicy() {}

    static void validate(FileCategory category, String originalName, String contentType, long fileSize) {
        Set<String> allowedContentTypes = ALLOWED_TYPES.get(category).get(extractExtension(originalName));
        if (allowedContentTypes == null) {
            throw new BusinessException(FileErrorCode.UNSUPPORTED_FILE_EXTENSION);
        }
        if (!allowedContentTypes.contains(contentType)) {
            throw new BusinessException(FileErrorCode.CONTENT_TYPE_MISMATCH);
        }
        if (fileSize <= 0 || fileSize > MAX_FILE_SIZE.get(category)) {
            throw new BusinessException(FileErrorCode.FILE_SIZE_EXCEEDED);
        }
    }

    static String extractExtension(String originalName) {
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == originalName.length() - 1) {
            return "";
        }
        return originalName.substring(dotIndex + 1).toLowerCase();
    }

    // 두 표에 같은 확장자가 있으면 어느 MIME을 쓸지 정할 수 없으므로, 합칠 때 예외로 드러낸다
    private static Map<String, Set<String>> merge(Map<String, Set<String>> first, Map<String, Set<String>> second) {
        return Stream.concat(first.entrySet().stream(), second.entrySet().stream())
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
