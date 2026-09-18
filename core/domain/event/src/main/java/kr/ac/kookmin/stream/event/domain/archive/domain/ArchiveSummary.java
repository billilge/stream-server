package kr.ac.kookmin.stream.event.domain.archive.domain;

import java.time.LocalDate;

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
            archive.thumbnailFileId()
        );
    }
}
