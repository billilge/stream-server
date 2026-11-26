package kr.ac.kookmin.stream.api.app.welfare.chat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.welfare.chat.response.ChatFaqResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;

/**
 * 학생 앱 챗봇 추천 질문 API의 문서 명세. 구현은 {@link AppChatFaqController}가 맡는다.
 */
@Tag(name = "챗봇", description = "학생 앱 챗봇 질문·답변")
public interface AppChatFaqApi {

    @Operation(summary = "추천 질문 목록 조회",
        description = "챗봇 진입 화면에 보여 줄 추천 질문을 등록 순서대로 조회한다. 최대 5개이고, 없으면 빈 목록이다. "
            + "누르면 question을 그대로 질문 보내기 API로 보내면 된다.")
    ApiResponse<List<ChatFaqResponse>> getFaqs(AppApiUser apiUser);
}
