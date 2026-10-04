package kr.ac.kookmin.stream.api.app.welfare.chat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.welfare.chat.request.ChatMessageCreateRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 학생 앱 챗봇 API의 문서 명세. 구현은 {@link AppChatController}가 맡는다.
 * <p>
 * <b>이 API만 {@code ApiResponse} 껍데기를 쓰지 않는다.</b> 껍데기는 완성된 응답 하나에 한 번 붙는
 * 구조인데, 답변은 조각으로 나눠 오므로 담을 자리가 없다. 조각마다 감싸면 껍데기가 수십 개가 되고
 * 조각마다 "요청에 성공했습니다"가 실려 나간다.
 * <p>
 * 그래서 에러가 두 갈래로 갈린다.
 * <ul>
 *   <li>스트림 시작 <b>전</b>(검증·인증 실패) — 기존대로 {@code ApiResponse} 에러와 상태 코드</li>
 *   <li>스트림 시작 <b>후</b>(AI 서버 실패·타임아웃) — {@code event: error}</li>
 * </ul>
 * 첫 바이트가 나간 뒤에는 상태 코드를 바꿀 수 없어 {@code GlobalExceptionHandler}가 잡지 못한다.
 */
@Tag(name = "챗봇", description = "학생 앱 챗봇 질문·답변")
public interface AppChatApi {

    /**
     * 질문을 보내고 답변을 SSE로 받는다.
     * <p>
     * 응답은 {@code text/event-stream}이고 이벤트는 네 종류다.
     * <pre>
     * event: delta   data: { "text": "부분 응답" }     0회 이상 반복
     * event: bubble  data: {}                          여기서부터 새 말풍선
     * event: done    data: {}                          정상 종료
     * event: error   data: { "message": "오류 안내" }   실패
     * </pre>
     * 프론트는 텍스트에서 빈 줄을 직접 찾지 않는다. 스트림 조각이 줄바꿈 중간에서 끊기면 판단이
     * 흔들리므로 서버가 문단 경계마다 {@code bubble}을 보낸다.
     */
    @Operation(summary = "챗봇 질문 전송 (SSE)",
        description = "질문을 보내고 답변을 조각으로 받는다. 응답은 JSON 한 번이 아니라 "
            + "text/event-stream이며 delta·bubble·done·error 네 이벤트가 온다. "
            + "지난 대화는 서버가 DB에서 꺼내 함께 보내므로 요청에는 이번 질문만 담는다.")
    void sendMessage(AppApiUser apiUser, ChatMessageCreateRequest request, HttpServletResponse response)
        throws IOException;
}
