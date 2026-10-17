package kr.ac.kookmin.stream.api.admin.event.event.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestionCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.QuestionType;

/**
 * 신청서 질문 한 건. 행사 등록·수정 요청이 함께 쓴다. 노출 순서는 목록에서의 위치로 정하므로 따로 받지 않는다.
 */
public record EventQuestionRequest(
    @Schema(description = "질문 유형")
    @NotNull(message = "질문 유형을 입력해 주세요.")
    QuestionType questionType,

    @Schema(description = "질문 내용", example = "참여 종목")
    @NotBlank(message = "질문 내용을 입력해 주세요.")
    @Size(max = 500, message = "질문 내용은 500자 이하로 입력해 주세요.")
    String questionText,

    @Schema(description = "필수 응답 여부")
    @NotNull(message = "필수 응답 여부를 입력해 주세요.")
    Boolean isRequired,

    @Schema(description = "선택지 목록. 선택형이면 1개 이상 필요하고, 텍스트형이면 무시한다", example = "[\"축구\", \"농구\"]")
    List<@NotBlank(message = "선택지는 비워 둘 수 없습니다.") String> options,

    @Schema(description = "'기타'(직접 입력) 선택지 추가 여부. 선택형에서만 쓰며 기본값은 false")
    Boolean hasOtherOption
) {

    public EventQuestionCommand toCommand() {
        return new EventQuestionCommand(
            questionText,
            questionType,
            isRequired,
            options == null ? List.of() : options,
            Boolean.TRUE.equals(hasOtherOption)
        );
    }
}
