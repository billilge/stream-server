package kr.ac.kookmin.stream.api.app.welfare.notice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.ApiErrorCode;
import kr.ac.kookmin.stream.ApiResponse;
import kr.ac.kookmin.stream.CursorSliceResponse;
import kr.ac.kookmin.stream.api.app.welfare.notice.response.NoticeDetailResponse;
import kr.ac.kookmin.stream.api.app.welfare.notice.response.NoticeListItemResponse;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeErrorCode;

/**
 * 학생 앱 공지 API의 문서 명세. 구현은 {@link AppNoticeController}가 맡는다.
 * <p>
 * 스웨거 문서용 어노테이션만 이쪽에 두고 컨트롤러에는 라우팅과 본문만 남긴다. 경로 매핑과
 * 파라미터 바인딩(@{@code RequestParam}, @{@code PathVariable} 등)은 구현체에 둔다.
 */
@Tag(name = "공지", description = "학생 앱 공지사항 조회")
public interface AppNoticeApi {

    /** 공지 목록. 커서 기반 페이지네이션이며 카테고리로 필터링한다. */
    @Operation(summary = "공지 목록 조회",
        description = "커서 기반으로 공지 목록을 조회한다. category로 분류를 필터링하고, cursor/size로 다음 페이지를 넘긴다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = NoticeErrorCode.class, codes = {"NOTICE_INVALID_CURSOR"})
    ApiResponse<CursorSliceResponse<NoticeListItemResponse>> getNotices(
        String category,
        String cursor,
        int size
    );

    /** 공지 상세. */
    @Operation(summary = "공지 상세 조회")
    @ApiErrorCode(type = NoticeErrorCode.class, codes = {"NOTICE_NOT_FOUND"})
    ApiResponse<NoticeDetailResponse> getNotice(Long noticeId);
}
