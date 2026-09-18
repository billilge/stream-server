package kr.ac.kookmin.stream.api.app.event.archive.response;

import java.util.List;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveSummary;

public record ArchiveListResponse(List<ArchiveListItemResponse> archives, List<Integer> years) {

    public static ArchiveListResponse of(List<ArchiveSummary> archives, List<Integer> years) {
        return new ArchiveListResponse(
            archives.stream().map(ArchiveListItemResponse::from).toList(),
            years
        );
    }
}
