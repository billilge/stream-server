package kr.ac.kookmin.stream.api.app.search;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.search.request.SearchParams;
import kr.ac.kookmin.stream.api.app.search.response.SearchResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 학생 앱 통합 검색 API의 문서 명세. 구현은 {@link AppSearchController}가 맡는다.
 * <p>
 * 스웨거 문서용 어노테이션만 이쪽에 두고 컨트롤러에는 라우팅과 본문만 남긴다.
 */
@Tag(name = "통합 검색", description = "학생 앱 통합 검색")
public interface AppSearchApi {

    @Operation(summary = "통합 검색",
        description = "검색어 하나로 행사·공지·열린피드백·빌릴게(물품)·아카이빙을 한 번에 찾는다. "
            + "행사·공지·아카이빙은 제목, 물품은 이름, 열린피드백은 질문 내용에서 부분 일치로 찾는다(앞뒤 공백 무시). "
            + "섹션마다 각 목록과 같은 노출 조건·순서로 앞의 2건과 전체 수를 주고, totalCount는 다섯 섹션 전체 수의 합이다. "
            + "더보기와 탭 목록은 각 목록 API(GET /v1/app/events, /v1/app/notices, /v1/app/feedbacks, "
            + "/v1/app/billilge/items, /v1/app/archives)에 같은 keyword를 넘겨 조회한다. "
            + "행사 카드의 applicationId는 행사 목록과 같이 요청한 학생이 그 행사에 낸 유효 신청의 ID이며, 없으면 null이다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    ApiResponse<SearchResponse> search(AppApiUser apiUser, @ParameterObject SearchParams params);
}
