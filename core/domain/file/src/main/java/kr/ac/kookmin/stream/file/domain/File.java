package kr.ac.kookmin.stream.file.domain;

import java.util.Objects;
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

    public static File create(
        String fileKey,
        FileCategory category,
        String originalName,
        long fileSize,
        String contentType,
        Long uploaderId
    ) {
        return new File(null, fileKey, category, originalName, fileSize, contentType, uploaderId);
    }

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

    // 파일 삭제와 회비 인증 이미지 검증이 같은 기준으로 "본인이 올린 파일인지"를 본다
    public boolean isUploadedBy(Long memberId) {
        return Objects.equals(uploaderId, memberId);
    }
}
