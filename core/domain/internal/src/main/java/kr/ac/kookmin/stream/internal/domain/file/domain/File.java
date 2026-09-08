package kr.ac.kookmin.stream.internal.domain.file.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class File {

    private Long id;
    private String fileKey;
    private FileCategory category;
    private String originalName;
    private long fileSize;
    private String contentType;
    private Long uploaderId;

    public static File of(
        Long id,
        String fileKey,
        FileCategory category,
        String originalName,
        long fileSize,
        String contentType,
        Long uploaderId
    ) {
        return new File(id, fileKey, category, originalName, fileSize, contentType, uploaderId);
    }
}
