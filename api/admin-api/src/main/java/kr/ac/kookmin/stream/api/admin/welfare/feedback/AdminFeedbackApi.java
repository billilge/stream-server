package kr.ac.kookmin.stream.api.admin.welfare.feedback;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.welfare.feedback.response.AdminFeedbackResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 운영진 열린피드백 질문 API의 문서 명세. 구현은 {@link AdminFeedbackController}가 맡는다.
 * <p>
 * 스웨거 문서용 어노테이션만 이쪽에 두고 컨트롤러에는 라우팅과 본문만 남긴다.
 */
@Tag(name = "열린피드백 질문 관리", description = "운영진 열린피드백 질문 조회")
public interface AdminFeedbackApi {

    /** 현재 회차 질문 탭. 질문 내용·작성자 이름으로 검색한다. */
    @Operation(summary = "사용자 질문 조회",
        description = "회차(roundId)의 질문을 질문 시각 내림차순으로 조회한다. roundId를 생략하면 진행 중 회차를 보고, "
            + "진행 중 회차도 없으면 빈 목록이다. keyword는 질문 내용과 작성자 이름에 부분 일치로 걸린다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = FeedbackErrorCode.class, codes = {"FEEDBACK_ROUND_NOT_FOUND"})
    ApiResponse<PageResponse<AdminFeedbackResponse>> getFeedbacks(
        AdminApiUser apiUser,
        Long roundId,
        String keyword,
        @ParameterObject PageParams pageParams
    );
}
