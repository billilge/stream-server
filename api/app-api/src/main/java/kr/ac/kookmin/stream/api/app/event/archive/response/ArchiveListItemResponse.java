package kr.ac.kookmin.stream.api.app.event.archive.response;

import java.time.LocalDate;
import java.util.Map;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveSummary;
import kr.ac.kookmin.stream.file.domain.File;

public record ArchiveListItemResponse(
    Long archiveId,
    String title,
    LocalDate startDate,
    LocalDate endDate,
    String thumbnailUrl
) {

    public static ArchiveListItemResponse from(ArchiveSummary summary, Map<Long, File> filesById) {
        return new ArchiveListItemResponse(
            summary.archiveId(),
            summary.title(),
            summary.startDate(),
            summary.endDate(),
            StorageUrlBuilder.build(summary.thumbnailFileId(), filesById)
        );
    }
}
