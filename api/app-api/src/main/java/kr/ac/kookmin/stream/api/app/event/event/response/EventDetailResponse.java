package kr.ac.kookmin.stream.api.app.event.event.response;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.event.domain.event.domain.EventDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;

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

    public static EventDetailResponse from(EventDetail detail) {
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
            detail.imageIds().stream().map(Image::from).toList()
        );
    }

    public record Image(Long fileId, String fileUrl) {

        /**
         * 파일 키 → 공개 URL 조립(#17)이 아직 없어 URL은 비어 있다. #17이 머지되면 이 팩토리만 채우면 된다.
         */
        public static Image from(Long fileId) {
            return new Image(fileId, null);
        }
    }
}
