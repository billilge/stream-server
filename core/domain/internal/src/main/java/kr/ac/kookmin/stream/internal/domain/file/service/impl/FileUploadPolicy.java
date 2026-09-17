package kr.ac.kookmin.stream.internal.domain.file.service.impl;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.internal.domain.file.domain.FileCategory;
import kr.ac.kookmin.stream.internal.domain.file.domain.FileErrorCode;

final class FileUploadPolicy {

    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> ATTACHMENT_EXTENSIONS = Set.of("pdf", "doc", "docx", "hwp", "zip");

    private static final long IMAGE_MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final long ATTACHMENT_MAX_FILE_SIZE = 20 * 1024 * 1024;

    private static final Map<FileCategory, Set<String>> ALLOWED_EXTENSIONS = Map.of(
        FileCategory.TEMP, union(IMAGE_EXTENSIONS, ATTACHMENT_EXTENSIONS),
        FileCategory.NOTICE_ATTACHMENT, ATTACHMENT_EXTENSIONS,
        FileCategory.NOTICE_IMAGE, IMAGE_EXTENSIONS,
        FileCategory.EVENT_IMAGE, IMAGE_EXTENSIONS,
        FileCategory.ARCHIVE_IMAGE, IMAGE_EXTENSIONS
    );

    private static final Map<FileCategory, Long> MAX_FILE_SIZE = Map.of(
        FileCategory.TEMP, ATTACHMENT_MAX_FILE_SIZE,
        FileCategory.NOTICE_ATTACHMENT, ATTACHMENT_MAX_FILE_SIZE,
        FileCategory.NOTICE_IMAGE, IMAGE_MAX_FILE_SIZE,
        FileCategory.EVENT_IMAGE, IMAGE_MAX_FILE_SIZE,
        FileCategory.ARCHIVE_IMAGE, IMAGE_MAX_FILE_SIZE
    );

    private FileUploadPolicy() {}

    static void validate(FileCategory category, String originalName, long fileSize) {
        String extension = extractExtension(originalName);
        if (!ALLOWED_EXTENSIONS.get(category).contains(extension)) {
            throw new BusinessException(FileErrorCode.UNSUPPORTED_FILE_EXTENSION);
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
