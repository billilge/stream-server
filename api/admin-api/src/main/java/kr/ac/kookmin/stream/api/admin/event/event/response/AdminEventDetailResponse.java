package kr.ac.kookmin.stream.api.admin.event.event.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.common.DateUtil;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventManageDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.event.domain.event.domain.QuestionType;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitType;
import kr.ac.kookmin.stream.file.domain.File;

/**
 * 수정 화면의 초기값. 필드 이름은 등록·수정 요청 본문과 맞춘다.
 *
 * @param capacity  선착순 모집의 정원. 상시 모집이면 {@code null}
 * @param updatedAt 마지막 수정 시각(KST 기준 epoch 밀리초). 수정 요청에 그대로 돌려보내 동시 수정을 감지한다
 */
public record AdminEventDetailResponse(
    Long eventId,
    String title,
    String description,
    String place,
    LocalDateTime eventStartAt,
    LocalDateTime eventEndAt,
    LocalDateTime applyStartAt,
    LocalDateTime applyEndAt,
    RecruitType recruitType,
    Integer capacity,
    boolean feePaidOnly,
    long applicantCount,
    RecruitStatus recruitStatus,
    boolean isPublished,
    List<Image> images,
    List<Question> questions,
    long updatedAt
) {

    /**
     * 존재하지 않는(삭제된) fileId는 imagesById에 키가 없다 — 그런 항목은 목록에서 조용히 제외한다.
     * 모집 상태는 저장값이 아니라 {@code now} 기준으로 행사가 계산한다.
     */
    public static AdminEventDetailResponse of(EventManageDetail detail, Map<Long, File> imagesById, LocalDateTime now) {
        Event event = detail.event();
        return new AdminEventDetailResponse(
            event.getId(),
            event.getTitle(),
            event.getDescription(),
            event.getPlace(),
            event.getEventStartAt(),
            event.getEventEndAt(),
            event.getApplyStartAt(),
            event.getApplyEndAt(),
            event.getRecruitType(),
            event.capacityLimit(),
            event.isFeePaidOnly(),
            detail.applicantCount(),
            event.calculateRecruitStatus(now, detail.applicantCount()),
            event.isPublished(),
            detail.imageIds().stream()
                .filter(imagesById::containsKey)
                .map(fileId -> Image.of(fileId, imagesById.get(fileId)))
                .toList(),
            detail.questions().stream().map(Question::from).toList(),
            event.getUpdatedAt().atZone(DateUtil.KST).toInstant().toEpochMilli()
        );
    }

    public record Image(Long fileId, String fileUrl) {

        public static Image of(Long fileId, File file) {
            return new Image(fileId, StorageUrlBuilder.build(file.getFileKey()));
        }
    }

    public record Question(
        Long questionId,
        QuestionType questionType,
        String questionText,
        boolean isRequired,
        int displayOrder,
        List<String> options,
        boolean hasOtherOption
    ) {

        public static Question from(EventQuestion question) {
            return new Question(
                question.getId(),
                question.getQuestionType(),
                question.getQuestionText(),
                question.isRequired(),
                question.getDisplayOrder(),
                question.getOptions() == null ? List.of() : question.getOptions(),
                question.isOtherOptionAllowed()
            );
        }
    }
}
