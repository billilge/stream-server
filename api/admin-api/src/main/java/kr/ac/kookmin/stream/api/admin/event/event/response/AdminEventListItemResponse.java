package kr.ac.kookmin.stream.api.admin.event.event.response;

import java.time.LocalDateTime;
import java.util.Map;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitType;
import kr.ac.kookmin.stream.file.domain.File;

/**
 * @param thumbnailUrl 대표 이미지(이미지 목록의 첫 장) URL. 이미지가 없거나 파일이 삭제됐으면 {@code null}
 * @param capacity     선착순 모집의 정원. 상시 모집이면 {@code null}이고 화면에 "인원 제한 없음"으로 보인다
 */
public record AdminEventListItemResponse(
    Long eventId,
    String title,
    String thumbnailUrl,
    long applicantCount,
    RecruitType recruitType,
    Integer capacity,
    RecruitStatus recruitStatus,
    boolean isPublished,
    LocalDateTime applyStartAt,
    LocalDateTime applyEndAt
) {

    /**
     * 모집 상태는 저장값이 아니라 {@code now} 기준으로 행사가 계산한다. 목록 전체가 같은 시각을 봐야 하므로 호출부가 넘긴다.
     */
    public static AdminEventListItemResponse of(
        Event event,
        long applicantCount,
        Map<Long, File> filesById,
        LocalDateTime now
    ) {
        return new AdminEventListItemResponse(
            event.getId(),
            event.getTitle(),
            StorageUrlBuilder.build(event.thumbnailFileId(), filesById),
            applicantCount,
            event.getRecruitType(),
            event.capacityLimit(),
            event.calculateRecruitStatus(now, applicantCount),
            event.isPublished(),
            event.getApplyStartAt(),
            event.getApplyEndAt()
        );
    }
}
