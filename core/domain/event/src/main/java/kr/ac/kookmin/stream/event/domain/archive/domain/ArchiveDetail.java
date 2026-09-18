package kr.ac.kookmin.stream.event.domain.archive.domain;

import java.time.LocalDate;
import java.util.List;

/**
 * 아카이빙 상세 한 건. 현장 사진은 저장된 배열 순서가 곧 표시 순서다.
 */
public record ArchiveDetail(
    Long archiveId,
    String title,
    LocalDate startDate,
    LocalDate endDate,
    String location,
    String departmentName,
    String content,
    List<Long> imageIds,
    List<ArchiveRelatedLink> relatedLinks
) {

    public static ArchiveDetail of(Archive archive, List<ArchiveRelatedLink> relatedLinks) {
        return new ArchiveDetail(
            archive.getId(),
            archive.getTitle(),
            archive.getStartDate(),
            archive.getEndDate(),
            archive.getLocation(),
            archive.getDepartmentName(),
            archive.getContent(),
            archive.getImageIds() == null ? List.of() : archive.getImageIds(),
            relatedLinks
        );
    }
}
