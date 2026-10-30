package kr.ac.kookmin.stream.api.admin.event.archive.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import kr.ac.kookmin.stream.event.domain.archive.domain.Archive;

/**
 * @param endDate 당일 활동이면 {@code null}
 */
public record AdminArchiveListItemResponse(
    Long archiveId,
    String title,
    LocalDate startDate,
    LocalDate endDate,
    String location,
    String departmentName,
    boolean isExposed,
    LocalDateTime createdAt
) {

    public static AdminArchiveListItemResponse from(Archive archive) {
        return new AdminArchiveListItemResponse(
            archive.getId(),
            archive.getTitle(),
            archive.getStartDate(),
            archive.getEndDate(),
            archive.getLocation(),
            archive.getDepartmentName(),
            archive.isExposed(),
            archive.getCreatedAt()
        );
    }
}
