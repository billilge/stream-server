package kr.ac.kookmin.stream.event.domain.event.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 내 행사 신청 상세 한 건. 신청 시점 정보와 그 신청에서 제출한 질문별 답변을 함께 돌려주는 읽기 모델이다.
 * <p>
 * 취소 후 재신청한 경우 신청마다 답변이 따로 있으므로, 질문은 행사 것을 쓰되 답변은 이 신청 것만 붙인다.
 */
public record EventApplicationDetail(
    Long eventId,
    String title,
    String place,
    EventApplicationStatus applicationStatus,
    LocalDateTime eventStartAt,
    LocalDateTime appliedAt,
    LocalDateTime canceledAt,
    List<AnsweredQuestion> questions
) {

    /**
     * 행사의 질문 전체에 이 신청의 답변을 짝지어 상세를 만든다.
     *
     * @param questions 행사의 질문 목록. 표시 순서대로 들어온 순서를 그대로 유지한다
     * @param answers   이 신청에 저장된 답변. 신청 시 빈 답변은 저장되지 않으므로 질문보다 적을 수 있다
     */
    public static EventApplicationDetail of(
        Event event,
        EventApplication application,
        List<EventQuestion> questions,
        List<EventApplicationAnswer> answers
    ) {
        // 질문당 답변은 한 건이지만, 과거 데이터가 어긋나더라도 조회가 깨지지 않도록 먼저 들어온 답변을 남긴다
        Map<Long, EventApplicationAnswer> answerByQuestionId = answers.stream()
            .collect(Collectors.toMap(
                EventApplicationAnswer::getEventQuestionId,
                Function.identity(),
                (first, second) -> first));

        return new EventApplicationDetail(
            event.getId(),
            event.getTitle(),
            event.getPlace(),
            application.getStatus(),
            event.getEventStartAt(),
            application.getAppliedAt(),
            application.getCanceledAt(),
            questions.stream()
                .map(question -> AnsweredQuestion.of(question, answerByQuestionId.get(question.getId())))
                .toList()
        );
    }

    /**
     * 질문 한 건과 그 신청에서 제출한 답변. 답하지 않은 선택 질문은 답변이 비어 있다.
     */
    public record AnsweredQuestion(EventQuestion question, String answerText, List<Integer> selectedOptions) {

        /**
         * @param answer 이 질문에 대한 답변. 답하지 않은 선택 질문이면 {@code null}이다
         */
        public static AnsweredQuestion of(EventQuestion question, EventApplicationAnswer answer) {
            if (answer == null) {
                return new AnsweredQuestion(question, null, List.of());
            }
            List<Integer> selectedOptions = answer.getSelectedOptions();
            return new AnsweredQuestion(
                question,
                answer.getAnswerText(),
                selectedOptions == null ? List.of() : selectedOptions
            );
        }
    }
}
