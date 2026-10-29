package kr.ac.kookmin.stream.event.domain.archive.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 아카이빙 상세 한 건. 현장 사진은 저장된 배열 순서가 곧 표시 순서다.
 * <p>
 * 학생 앱과 운영진이 같이 쓴다. 담는 값이 거의 같고 운영진만 보는 것은 두 개(슬랑제 노출 여부·등록 일시)뿐이라
 * 하나로 두고, 학생 앱 응답이 그 둘을 담지 않는다.
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
    List<ArchiveRelatedLink> relatedLinks,
    boolean exposed,
    LocalDateTime createdAt
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
            relatedLinks,
            archive.isExposed(),
            archive.getCreatedAt()
        );
    }
}
