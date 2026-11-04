package kr.ac.kookmin.stream.api.app.welfare.chat.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 챗봇에 보낼 질문. 지난 대화는 서버가 DB에서 꺼내 붙이므로 이번 질문만 받는다.
 * <p>
 * 길이를 제한하는 이유는 두 가지다. 긴 글을 그대로 넘기면 토큰 비용이 그만큼 늘고,
 * 프롬프트 인젝션을 길게 심을 여지를 줄인다.
 * <p>
 * {@code conversationId}는 채팅방 입장 한 번을 하나의 대화로 묶는다. 앱은 들어올 때마다 새 대화로 보여주므로
 * 회원 기준으로 이력을 붙이면 화면은 비어 있는데 봇이 이전 대화를 기억한다.
 * {@code UUID} 타입으로 받지 않고 문자열 + 정규식으로 검증하는 이유는, 형식이 틀렸을 때 본문 파싱 실패가 아니라
 * 검증 실패로 가서 어느 필드가 틀렸는지 응답에 담기 때문이다.
 */
public record ChatMessageCreateRequest(
    @Schema(description = "채팅방에 들어올 때 프론트가 만든 대화 id (UUID)", example = "3f1c9a2e-7b4d-4c1a-9e2f-5a6b7c8d9e0f")
    @NotBlank(message = "대화 id를 입력해주세요.")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
        message = "대화 id 형식이 올바르지 않습니다.")
    String conversationId,

    @Schema(description = "학생이 입력한 질문", example = "빌릴게 어떻게 써요?")
    @NotBlank(message = "질문을 입력해주세요.")
    @Size(max = 500, message = "질문은 500자까지 입력할 수 있습니다.")
    String message
) {}
