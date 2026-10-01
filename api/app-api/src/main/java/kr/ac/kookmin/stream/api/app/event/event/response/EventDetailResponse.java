package kr.ac.kookmin.stream.api.app.event.event.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.event.domain.event.domain.EventDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;
import kr.ac.kookmin.stream.file.domain.File;

public record EventDetailResponse(
    Long eventId,
    String title,
    String description,
    String target,
    String place,
    LocalDateTime eventStartAt,
    LocalDateTime eventEndAt,
    LocalDateTime applyStartAt,
    LocalDateTime applyEndAt,
    RecruitStatus recruitStatus,
    Integer daysUntilDeadline,
    List<Image> images
) {

    /**
     * 존재하지 않는(삭제된) fileId는 imagesById에 키가 없다 — 그런 항목은 목록에서 조용히 제외한다.
     */
    public static EventDetailResponse from(EventDetail detail, Map<Long, File> imagesById) {
        return new EventDetailResponse(
            detail.eventId(),
            detail.title(),
            detail.description(),
            detail.target(),
            detail.place(),
            detail.eventStartAt(),
            detail.eventEndAt(),
            detail.applyStartAt(),
            detail.applyEndAt(),
            detail.recruitStatus(),
            detail.daysUntilDeadline(),
            detail.imageIds().stream()
                .filter(imagesById::containsKey)
                .map(fileId -> Image.of(fileId, imagesById.get(fileId)))
                .toList()
        );
    }

    public record Image(Long fileId, String fileUrl) {

        public static Image of(Long fileId, File file) {
            return new Image(fileId, StorageUrlBuilder.build(file.getFileKey()));
        }
    }
}
