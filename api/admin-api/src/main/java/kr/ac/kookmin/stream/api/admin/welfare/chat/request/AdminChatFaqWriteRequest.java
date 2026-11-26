package kr.ac.kookmin.stream.api.admin.welfare.chat.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * @param question 누르면 그대로 챗봇에 가므로 챗봇 질문 상한(500자)보다 짧아야 한다
 */
public record AdminChatFaqWriteRequest(

    @Schema(description = "추천 질문", example = "사물함 신청은 어떻게 하나요?")
    @NotBlank(message = "질문을 입력해 주세요.")
    @Size(max = 100, message = "질문은 100자까지 입력할 수 있습니다.")
    String question
) {
}
