package kr.ac.kookmin.stream.api.app.event.archive.response;

import java.util.List;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveList;

public record ArchiveListResponse(List<ArchiveListItemResponse> archives, List<Integer> years) {

    public static ArchiveListResponse from(ArchiveList archiveList) {
        return new ArchiveListResponse(
            archiveList.archives().stream().map(ArchiveListItemResponse::from).toList(),
            archiveList.years()
        );
    }
}
