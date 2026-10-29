package kr.ac.kookmin.stream.api.admin.event.event.response;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.api.admin.event.event.usecase.EventApplicantDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.event.domain.event.domain.QuestionType;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;

/**
 * 신청자 상세(폼 조회). 신청 현황 표에서 잘린 답변을 질문 전체와 함께 보여준다.
 *
 * @param name      탈퇴한 회원의 신청이면 {@code null}
 * @param studentId 탈퇴한 회원의 신청이면 {@code null}
 */
public record AdminEventApplicationDetailResponse(
    Long applicationId,
    String name,
    String studentId,
    LocalDateTime appliedAt,
    EventApplicationStatus status,
    LocalDateTime canceledAt,
    List<AnsweredQuestion> questions
) {

    public static AdminEventApplicationDetailResponse from(EventApplicantDetail applicant) {
        Member member = applicant.member();
        EventApplicationDetail detail = applicant.detail();
        return new AdminEventApplicationDetailResponse(
            applicant.application().getId(),
            member == null ? null : member.getName(),
            member == null ? null : member.getStudentId(),
            detail.appliedAt(),
            detail.applicationStatus(),
            detail.canceledAt(),
            detail.questions().stream().map(AnsweredQuestion::from).toList()
        );
    }

    /**
     * 질문 한 건과 이 신청의 답변. 학생 앱 내 신청 상세와 같은 모양이다.
     *
     * @param answerText      텍스트형 답변. 선택형이거나 답하지 않았으면 {@code null}
     * @param selectedOptions 고른 선택지의 0부터 시작하는 순서 번호. 텍스트형이거나 답하지 않았으면 빈 배열
     */
    public record AnsweredQuestion(
        Long questionId,
        String questionText,
        QuestionType questionType,
        boolean isRequired,
        int displayOrder,
        List<String> options,
        String answerText,
        List<Integer> selectedOptions
    ) {

        public static AnsweredQuestion from(EventApplicationDetail.AnsweredQuestion answeredQuestion) {
            EventQuestion question = answeredQuestion.question();
            return new AnsweredQuestion(
                question.getId(),
                question.getQuestionText(),
                question.getQuestionType(),
                question.isRequired(),
                question.getDisplayOrder(),
                question.getOptions(),
                answeredQuestion.answerText(),
                answeredQuestion.selectedOptions()
            );
        }
    }
}
