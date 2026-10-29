package kr.ac.kookmin.stream.api.app.event.archive.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveDetail;
import kr.ac.kookmin.stream.file.domain.File;

public record ArchiveDetailResponse(
    Long archiveId,
    String title,
    LocalDate startDate,
    LocalDate endDate,
    String location,
    String departmentName,
    String content,
    List<Image> images,
    RelatedEvent relatedEvent
) {

    /**
     * 존재하지 않는(삭제된) fileId는 imagesById에 키가 없다 — 그런 항목은 목록에서 조용히 제외한다.
     *
     * @param relatedEventTitle 연결한 행사의 제목. 연결이 없거나 행사가 사라졌으면 {@code null}
     */
    public static ArchiveDetailResponse of(
        ArchiveDetail detail, String relatedEventTitle, Map<Long, File> imagesById
    ) {
        return new ArchiveDetailResponse(
            detail.archiveId(),
            detail.title(),
            detail.startDate(),
            detail.endDate(),
            detail.location(),
            detail.departmentName(),
            detail.content(),
            detail.imageIds().stream()
                .filter(imagesById::containsKey)
                .map(fileId -> Image.of(fileId, imagesById.get(fileId)))
                .toList(),
            RelatedEvent.of(detail.relatedEventId(), relatedEventTitle)
        );
    }

    /** 눌러서 이동할 행사. */
    public record RelatedEvent(Long eventId, String title) {

        /** 연결이 없거나 행사가 사라져 제목을 못 가져왔으면 {@code null}이다. */
        public static RelatedEvent of(Long eventId, String title) {
            return eventId == null || title == null ? null : new RelatedEvent(eventId, title);
        }
    }

    public record Image(Long fileId, String fileUrl) {

        public static Image of(Long fileId, File file) {
            return new Image(fileId, StorageUrlBuilder.build(file.getFileKey()));
        }
    }
}
