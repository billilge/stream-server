package kr.ac.kookmin.stream.file.service.impl;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.file.domain.FileCategory;
import kr.ac.kookmin.stream.file.domain.FileErrorCode;

final class FileUploadPolicy {

    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> ATTACHMENT_EXTENSIONS = Set.of("pdf", "doc", "docx", "hwp", "zip");
    // 빌릴게 물품 아이콘은 SVG로만 받는다
    private static final Set<String> ITEM_ICON_EXTENSIONS = Set.of("svg");

    private static final long IMAGE_MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final long ATTACHMENT_MAX_FILE_SIZE = 20 * 1024 * 1024;

    private static final Map<FileCategory, Set<String>> ALLOWED_EXTENSIONS = Map.of(
        FileCategory.TEMP, union(IMAGE_EXTENSIONS, ATTACHMENT_EXTENSIONS),
        FileCategory.NOTICE_ATTACHMENT, ATTACHMENT_EXTENSIONS,
        FileCategory.NOTICE_IMAGE, IMAGE_EXTENSIONS,
        FileCategory.EVENT_IMAGE, IMAGE_EXTENSIONS,
        FileCategory.ARCHIVE_IMAGE, IMAGE_EXTENSIONS,
        FileCategory.LOCKER_SECTION_PHOTO, IMAGE_EXTENSIONS,
        FileCategory.FEE_TRANSCRIPT_IMAGE, IMAGE_EXTENSIONS,
        FileCategory.ITEM_IMAGE, ITEM_ICON_EXTENSIONS
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

    // 받은 contentType을 그대로 서명해 저장소가 그 타입으로 내려주므로, 확장자와 맞는 MIME만 받는다
    // image/jpg는 일부 모바일 앱, x-zip-compressed는 Windows, haansofthwp는 한컴 설치 환경이 보내는 값이다
    private static final Map<String, Set<String>> ALLOWED_CONTENT_TYPES = Map.of(
        "jpg", Set.of("image/jpeg", "image/jpg"),
        "jpeg", Set.of("image/jpeg", "image/jpg"),
        "png", Set.of("image/png"),
        "webp", Set.of("image/webp"),
        "svg", Set.of("image/svg+xml"),
        "pdf", Set.of("application/pdf"),
        "doc", Set.of("application/msword"),
        "docx", Set.of("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
        "hwp", Set.of("application/x-hwp", "application/haansofthwp"),
        "zip", Set.of("application/zip", "application/x-zip-compressed")
    );

    private FileUploadPolicy() {}

    static void validate(FileCategory category, String originalName, String contentType, long fileSize) {
        String extension = extractExtension(originalName);
        if (!ALLOWED_EXTENSIONS.get(category).contains(extension)) {
            throw new BusinessException(FileErrorCode.UNSUPPORTED_FILE_EXTENSION);
        }
        if (!ALLOWED_CONTENT_TYPES.getOrDefault(extension, Set.of()).contains(contentType)) {
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

    private static Set<String> union(Set<String> first, Set<String> second) {
        return Stream.concat(first.stream(), second.stream())
            .collect(Collectors.toUnmodifiableSet());
    }
}
