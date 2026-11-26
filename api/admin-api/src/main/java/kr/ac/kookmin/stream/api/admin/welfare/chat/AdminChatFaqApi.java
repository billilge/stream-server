package kr.ac.kookmin.stream.api.admin.welfare.chat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.welfare.chat.request.AdminChatFaqWriteRequest;
import kr.ac.kookmin.stream.api.admin.welfare.chat.response.AdminChatFaqResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.welfare.domain.chat.domain.ChatErrorCode;
import kr.ac.kookmin.stream.welfare.domain.chat.domain.ChatFaq;

/**
 * 운영진 챗봇 추천 질문 API의 문서 명세. 구현은 {@link AdminChatFaqController}가 맡는다.
 */
@Tag(name = "챗봇 추천 질문 관리", description = "운영진 챗봇 추천 질문(FAQ) 조회·등록·수정·삭제")
public interface AdminChatFaqApi {

    @Operation(summary = "추천 질문 목록 조회", description = "등록 순서대로 전부 조회한다. 최대 " + ChatFaq.MAX_COUNT + "개라 페이지로 자르지 않는다.")
    ApiResponse<List<AdminChatFaqResponse>> getFaqs(AdminApiUser apiUser);

    @Operation(summary = "추천 질문 등록",
        description = "질문을 등록하고 목록 맨 뒤에 붙인다. 이미 " + ChatFaq.MAX_COUNT + "개면 거부한다. "
            + "다른 운영진과 동시에 등록해 부딪히면 한쪽만 들어가고 다른 쪽은 CHAT_FAQ_ADD_CONFLICT를 받는다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = ChatErrorCode.class, codes = {"CHAT_FAQ_LIMIT_EXCEEDED", "CHAT_FAQ_ADD_CONFLICT"})
    ApiResponse<Void> createFaq(AdminApiUser apiUser, AdminChatFaqWriteRequest request);

    @Operation(summary = "추천 질문 수정", description = "질문 문구를 바꾼다. 목록 순서는 그대로다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = ChatErrorCode.class, codes = {"CHAT_FAQ_NOT_FOUND"})
    ApiResponse<Void> updateFaq(AdminApiUser apiUser, Long faqId, AdminChatFaqWriteRequest request);

    @Operation(summary = "추천 질문 삭제", description = "질문을 삭제한다. 데이터는 남기고 목록과 앱에서만 보이지 않게 한다.")
    @ApiErrorCode(type = ChatErrorCode.class, codes = {"CHAT_FAQ_NOT_FOUND"})
    ApiResponse<Void> deleteFaq(AdminApiUser apiUser, Long faqId);
}
