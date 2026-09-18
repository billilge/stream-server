package kr.ac.kookmin.stream.event.domain.archive.domain;

import java.time.LocalDate;
import java.util.List;

/**
 * 아카이빙 목록 한 건. 대표 이미지는 저장된 사진 배열의 첫 장이다.
 */
public record ArchiveSummary(
    Long archiveId,
    String title,
    LocalDate startDate,
    LocalDate endDate,
    Long thumbnailFileId
) {

    public static ArchiveSummary from(Archive archive) {
        return new ArchiveSummary(
            archive.getId(),
            archive.getTitle(),
            archive.getStartDate(),
            archive.getEndDate(),
            thumbnailFileIdOf(archive.getImageIds())
        );
    }

    /**
     * 대표 이미지 파일 id. 사진 없는 아카이빙은 등록되지 않는다는 전제지만 최소 1장 제약이 없어,
     * 비어 있으면 예외 대신 null을 돌려 이상 데이터 한 건이 목록 전체를 막지 않게 한다.
     */
    private static Long thumbnailFileIdOf(List<Long> imageIds) {
        return imageIds == null || imageIds.isEmpty() ? null : imageIds.getFirst();
    }
}
