package kr.ac.kookmin.stream.file.service.impl;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.file.domain.FileCategory;
import kr.ac.kookmin.stream.file.domain.FileErrorCode;

final class FileUploadPolicy {

    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> ATTACHMENT_EXTENSIONS = Set.of("pdf", "doc", "docx", "hwp", "zip");
    private static final Set<String> ALL_EXTENSIONS = union(IMAGE_EXTENSIONS, ATTACHMENT_EXTENSIONS);

    private static final long IMAGE_MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final long ATTACHMENT_MAX_FILE_SIZE = 20 * 1024 * 1024;

    private FileUploadPolicy() {}

    static void validate(FileCategory category, String originalName, long fileSize) {
        String extension = extractExtension(originalName);
        if (!allowedExtensions(category).contains(extension)) {
            throw new BusinessException(FileErrorCode.UNSUPPORTED_FILE_EXTENSION);
        }
        if (fileSize <= 0 || fileSize > maxFileSize(category)) {
            throw new BusinessException(FileErrorCode.FILE_SIZE_EXCEEDED);
        }
    }

    // default를 두지 않는다. 카테고리가 늘면 컴파일이 막으므로 정책을 빠뜨린 카테고리가 업로드 시점의
    // NullPointerException(500)으로 새지 않는다
    private static Set<String> allowedExtensions(FileCategory category) {
        return switch (category) {
            case TEMP -> ALL_EXTENSIONS;
            case NOTICE_ATTACHMENT -> ATTACHMENT_EXTENSIONS;
            case NOTICE_IMAGE, EVENT_IMAGE, ARCHIVE_IMAGE, LOCKER_SECTION_PHOTO,
                FEE_TRANSCRIPT_IMAGE, ITEM_IMAGE, HOME_BANNER_IMAGE -> IMAGE_EXTENSIONS;
        };
    }

    private static long maxFileSize(FileCategory category) {
        return switch (category) {
            case TEMP, NOTICE_ATTACHMENT -> ATTACHMENT_MAX_FILE_SIZE;
            case NOTICE_IMAGE, EVENT_IMAGE, ARCHIVE_IMAGE, LOCKER_SECTION_PHOTO,
                FEE_TRANSCRIPT_IMAGE, ITEM_IMAGE, HOME_BANNER_IMAGE -> IMAGE_MAX_FILE_SIZE;
        };
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
