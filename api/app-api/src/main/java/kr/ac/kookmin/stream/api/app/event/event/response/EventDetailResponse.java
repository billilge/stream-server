package kr.ac.kookmin.stream.api.app.event.event.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.event.domain.event.domain.EventDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitType;
import kr.ac.kookmin.stream.file.domain.File;

/**
 * @param capacity      선착순 모집의 정원. 상시 모집이면 {@code null}이다
 * @param isFeeEligible 요청한 학생이 학생회비 조건으로 신청할 수 있는지. 납부자 전용이 아니면 항상 {@code true}이고,
 *                      납부자 전용이면 납부자 명부에 있을 때만 {@code true}다. 모집 기간·정원은 보지 않는다
 * @param applicationId 요청한 학생이 이 행사에 낸 유효 신청의 식별자. 신청하지 않았거나 취소한 신청만 있으면
 *                      {@code null}이다. 모집 상태와 관계없이 내려준다
 */
public record EventDetailResponse(
    Long eventId,
    String title,
    String description,
    String place,
    LocalDateTime eventStartAt,
    LocalDateTime eventEndAt,
    LocalDateTime applyStartAt,
    LocalDateTime applyEndAt,
    RecruitStatus recruitStatus,
    Integer daysUntilDeadline,
    RecruitType recruitType,
    Integer capacity,
    boolean feePaidOnly,
    boolean isFeeEligible,
    Long applicationId,
    List<Image> images
) {

    /**
     * 존재하지 않는(삭제된) fileId는 imagesById에 키가 없다 — 그런 항목은 목록에서 조용히 제외한다.
     */
    public static EventDetailResponse of(
        EventDetail detail,
        boolean isFeeEligible,
        Long applicationId,
        Map<Long, File> imagesById
    ) {
        return new EventDetailResponse(
            detail.eventId(),
            detail.title(),
            detail.description(),
            detail.place(),
            detail.eventStartAt(),
            detail.eventEndAt(),
            detail.applyStartAt(),
            detail.applyEndAt(),
            detail.recruitStatus(),
            detail.daysUntilDeadline(),
            detail.recruitType(),
            detail.capacity(),
            detail.feePaidOnly(),
            isFeeEligible,
            applicationId,
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
