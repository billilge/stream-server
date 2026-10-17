package kr.ac.kookmin.stream.api.app.welfare.chat.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 챗봇에 보낼 질문. 지난 대화는 서버가 DB에서 꺼내 붙이므로 이번 질문만 받는다.
 * <p>
 * 길이를 제한하는 이유는 두 가지다. 긴 글을 그대로 넘기면 토큰 비용이 그만큼 늘고,
 * 프롬프트 인젝션을 길게 심을 여지를 줄인다.
 */
public record ChatMessageCreateRequest(
    @Schema(description = "학생이 입력한 질문", example = "빌릴게 어떻게 써요?")
    @NotBlank(message = "질문을 입력해주세요.")
    @Size(max = 500, message = "질문은 500자까지 입력할 수 있습니다.")
    String message
) {}
