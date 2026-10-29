package kr.ac.kookmin.stream.api.admin.event.event.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.api.admin.event.event.usecase.EventApplicant;
import kr.ac.kookmin.stream.api.admin.event.event.usecase.EventApplicantPage;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationAnswer;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;

/**
 * 신청 현황 화면. 질문은 답변 열의 헤더이고, 신청 한 줄의 답변은 같은 순서로 담는다.
 *
 * @param totalApplicationCount 취소를 포함한 전체 신청 수. 검색·필터와 상관없다
 * @param appliedCount          신청완료 수. 검색·필터와 상관없다
 */
public record AdminEventApplicationListResponse(
    Long eventId,
    String eventTitle,
    long totalApplicationCount,
    long appliedCount,
    List<Question> questions,
    PageResponse<Application> applications
) {

    public static AdminEventApplicationListResponse from(EventApplicantPage page) {
        Event event = page.event().event();
        List<EventQuestion> questions = page.event().questions();
        return new AdminEventApplicationListResponse(
            event.getId(),
            event.getTitle(),
            page.totalApplicationCount(),
            page.event().applicantCount(),
            questions.stream().map(Question::from).toList(),
            PageResponse.from(page.applicants(), applicant -> Application.of(applicant, questions))
        );
    }

    public record Question(Long questionId, String questionText) {

        public static Question from(EventQuestion question) {
            return new Question(question.getId(), question.getQuestionText());
        }
    }

    /**
     * @param name      탈퇴한 회원의 신청이면 {@code null}
     * @param studentId 탈퇴한 회원의 신청이면 {@code null}
     * @param answers   {@code questions}와 같은 순서
     */
    public record Application(
        Long applicationId,
        String name,
        String studentId,
        LocalDateTime appliedAt,
        EventApplicationStatus status,
        List<Answer> answers
    ) {

        public static Application of(EventApplicant applicant, List<EventQuestion> questions) {
            Member member = applicant.member();
            Map<Long, EventApplicationAnswer> answerByQuestionId = applicant.answers().stream()
                .collect(Collectors.toMap(
                    EventApplicationAnswer::getEventQuestionId, Function.identity(), (first, second) -> first));

            return new Application(
                applicant.application().getId(),
                member == null ? null : member.getName(),
                member == null ? null : member.getStudentId(),
                applicant.application().getAppliedAt(),
                applicant.application().getStatus(),
                questions.stream()
                    .map(question -> Answer.of(question, answerByQuestionId.get(question.getId())))
                    .toList()
            );
        }
    }

    /**
     * @param displayText 표에 보일 답변. 답하지 않았으면 {@code null}
     */
    public record Answer(Long questionId, String displayText) {

        /**
         * 텍스트형은 입력한 답을, 선택형은 고른 선택지 문구를 쉼표로 이어 보여준다.
         *
         * @param answer 이 질문에 대한 답변. 답하지 않았으면 {@code null}
         */
        public static Answer of(EventQuestion question, EventApplicationAnswer answer) {
            return new Answer(question.getId(), answer == null ? null : displayTextOf(question, answer));
        }

        private static String displayTextOf(EventQuestion question, EventApplicationAnswer answer) {
            if (!question.getQuestionType().isChoice()) {
                return answer.getAnswerText();
            }
            List<Integer> selected = answer.getSelectedOptions() == null ? List.of() : answer.getSelectedOptions();
            List<String> options = question.getOptions();
            // 선택지가 바뀌어 범위를 벗어난 인덱스는 건너뛴다. 신청이 있으면 질문을 못 바꾸지만 과거 데이터에 대비한다
            String text = selected.stream()
                .filter(index -> index >= 0 && index < options.size())
                .map(options::get)
                .collect(Collectors.joining(", "));
            return text.isEmpty() ? null : text;
        }
    }
}
