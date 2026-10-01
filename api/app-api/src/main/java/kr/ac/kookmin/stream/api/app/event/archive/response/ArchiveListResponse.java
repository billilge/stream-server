package kr.ac.kookmin.stream.api.app.event.archive.response;

import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveSummary;
import kr.ac.kookmin.stream.file.domain.File;

public record ArchiveListResponse(List<ArchiveListItemResponse> archives, List<Integer> years) {

    public static ArchiveListResponse of(List<ArchiveSummary> archives, List<Integer> years, Map<Long, File> filesById) {
        return new ArchiveListResponse(
            archives.stream().map(summary -> ArchiveListItemResponse.from(summary, filesById)).toList(),
            years
        );
    }
}
