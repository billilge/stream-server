package kr.ac.kookmin.stream.api.ai.event.archive.response;

import java.time.LocalDate;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveDetail;

/**
 * 챗봇 인덱싱용 아카이빙. 지난 활동이 "언제·어디서·무엇을" 했는지 답할 글만 담는다 — 이미지는 넣지 않는다.
 */
public record AiArchiveResponse(
    Long archiveId,
    String title,
    String content,
    String location,
    String departmentName,
    LocalDate startDate,
    LocalDate endDate
) {

    public static AiArchiveResponse from(ArchiveDetail archive) {
        return new AiArchiveResponse(
            archive.archiveId(),
            archive.title(),
            archive.content(),
            archive.location(),
            archive.departmentName(),
            archive.startDate(),
            archive.endDate()
        );
    }
}
