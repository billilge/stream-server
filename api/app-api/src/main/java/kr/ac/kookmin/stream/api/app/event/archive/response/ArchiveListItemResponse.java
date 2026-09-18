package kr.ac.kookmin.stream.api.app.event.archive.response;

import java.time.LocalDate;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveSummary;

public record ArchiveListItemResponse(
    Long archiveId,
    String title,
    LocalDate startDate,
    LocalDate endDate,
    String thumbnailUrl
) {

    public static ArchiveListItemResponse from(ArchiveSummary summary) {
        return new ArchiveListItemResponse(
            summary.archiveId(),
            summary.title(),
            summary.startDate(),
            summary.endDate(),
            thumbnailUrlOf(summary.thumbnailFileId())
        );
    }

    /**
     * 대표 이미지 파일 id를 공개 URL로 바꾼다.
     * <p>
     * 파일 키 → 공개 URL 조립이 아직 없어 현재는 항상 비어 있다. 조립이 생기면 이 메서드만 채우면 된다.
     */
    private static String thumbnailUrlOf(Long thumbnailFileId) {
        return null;
    }
}
