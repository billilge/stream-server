package kr.ac.kookmin.stream.api.admin.welfare.feedback.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackAnswerCommand;

/**
 * 답변 임시저장 요청. 화면이 질문 여러 개를 체크해 각각 답변을 쓰는 형태라 회차 단위 일괄로 받는다.
 * 보낸 것만 반영하고, 이미 답변이 있는 질문을 다시 보내면 덮어쓴다.
 */
public record FeedbackAnswerSaveRequest(

    @Schema(description = "답변 목록. 최소 1건")
    @NotEmpty(message = "답변을 1건 이상 입력해 주세요.")
    List<@Valid @NotNull(message = "답변을 입력해 주세요.") Item> answers
) {

    public List<FeedbackAnswerCommand> toCommands() {
        validateFeedbackIdsNotDuplicated();

        return answers.stream()
            .map(item -> FeedbackAnswerCommand.of(item.feedbackId(), item.answer()))
            .toList();
    }

    // 같은 질문이 두 번 오면 어느 답변이 남을지 요청만 보고 알 수 없다.
    // IllegalArgumentException → GlobalExceptionHandler가 INVALID_INPUT(400)으로 응답한다
    private void validateFeedbackIdsNotDuplicated() {
        long distinctFeedbackIdCount = answers.stream()
            .map(Item::feedbackId)
            .distinct()
            .count();
        if (distinctFeedbackIdCount != answers.size()) {
            throw new IllegalArgumentException("같은 질문의 답변이 중복되었습니다.");
        }
    }

    public record Item(

        @Schema(description = "답변할 피드백 아이디", example = "21")
        @NotNull(message = "답변할 피드백을 선택해 주세요.")
        Long feedbackId,

        // answer는 TEXT 컬럼(최대 65,535바이트)이다. 글자당 최대 4바이트로 잡아도 넘치지 않는 길이로 막는다
        @Schema(description = "답변 내용. `**텍스트**`로 감싼 부분은 굵게 렌더링하기로 한 약속이다",
            example = "시험 기간에는 열람실을 **24시간** 개방합니다.")
        @NotBlank(message = "답변 내용은 필수 입력값입니다.")
        @Size(max = 16000, message = "답변 내용은 16000자 이하로 입력해 주세요.")
        String answer
    ) {
    }
}
