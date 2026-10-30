package kr.ac.kookmin.stream.api.admin.event.archive.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveDetail;
import kr.ac.kookmin.stream.file.domain.File;

/**
 * 대표 이미지는 따로 담지 않는다. 사진 배열의 첫 장이 대표라 화면이 그대로 떼어 쓰면 되고, 학생 앱 상세
 * ({@code ArchiveDetailResponse})도 같은 모양이다.
 *
 * @param endDate 당일 활동이면 {@code null}
 * @param images  활동 사진. 노출 순서대로 담고, 없으면 빈 목록이다. 첫 장이 대표 이미지다
 */
public record AdminArchiveDetailResponse(
    Long archiveId,
    String title,
    LocalDate startDate,
    LocalDate endDate,
    String location,
    String departmentName,
    String content,
    boolean isExposed,
    List<Image> images,
    RelatedEvent relatedEvent,
    LocalDateTime createdAt
) {

    /**
     * @param relatedEventTitle 연결한 행사의 제목. 연결이 없거나 행사가 사라졌으면 {@code null}
     * @param imagesById        활동 사진 파일. 삭제된 파일은 키가 없어 목록에서 조용히 빠진다
     */
    public static AdminArchiveDetailResponse of(
        ArchiveDetail detail, String relatedEventTitle, Map<Long, File> imagesById
    ) {
        return new AdminArchiveDetailResponse(
            detail.archiveId(),
            detail.title(),
            detail.startDate(),
            detail.endDate(),
            detail.location(),
            detail.departmentName(),
            detail.content(),
            detail.exposed(),
            detail.imageIds().stream()
                .filter(imagesById::containsKey)
                .map(fileId -> Image.of(fileId, imagesById.get(fileId)))
                .toList(),
            RelatedEvent.of(detail.relatedEventId(), relatedEventTitle),
            detail.createdAt()
        );
    }

    /** 연결한 행사. 수정 화면이 드롭다운의 기존 값을 채울 때도 쓴다. */
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
