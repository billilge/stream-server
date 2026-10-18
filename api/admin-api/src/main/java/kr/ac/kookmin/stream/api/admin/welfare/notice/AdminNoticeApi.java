package kr.ac.kookmin.stream.api.admin.welfare.notice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.welfare.notice.request.NoticeCreateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.notice.request.NoticePinRequest;
import kr.ac.kookmin.stream.api.admin.welfare.notice.request.NoticeUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.notice.response.AdminNoticeDetailResponse;
import kr.ac.kookmin.stream.api.admin.welfare.notice.response.AdminNoticeListItemResponse;
import kr.ac.kookmin.stream.api.admin.welfare.notice.response.NoticeCreateResponse;
import kr.ac.kookmin.stream.api.admin.welfare.notice.response.NoticeUpdateResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.file.domain.FileErrorCode;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 운영진 공지 API의 문서 명세. 구현은 {@link AdminNoticeController}가 맡는다.
 * <p>
 * 접근은 {@code ADMIN} role이면 된다. 부서 제한은 두지 않았다.
 */
@Tag(name = "공지 관리", description = "운영진 공지 조회·작성·수정·삭제·고정")
public interface AdminNoticeApi {

    /** 공지 목록. 제목으로 검색하고 고정 공지를 먼저, 그다음 최신순으로 페이지 단위 조회한다. */
    @Operation(summary = "공지 목록 조회",
        description = "삭제되지 않은 공지를 고정 공지 우선, 최신순으로 페이지 단위로 조회한다. keyword를 주면 제목에 그 글자가 "
            + "들어 있는 공지만 조회한다. page는 0부터 시작한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    ApiResponse<PageResponse<AdminNoticeListItemResponse>> getNotices(
        AdminApiUser apiUser,
        String keyword,
        @ParameterObject PageParams pageParams
    );

    /** 공지 상세. 수정 화면이 이 값을 불러와 채우고, 수정할 때 updatedAt을 그대로 돌려 보낸다. */
    @Operation(summary = "공지 상세 조회",
        description = "공지 한 건을 조회한다. 수정 화면에 필요한 고정 여부, 이미지·첨부 파일의 ID와 URL, 수정 시각(updatedAt)을 "
            + "함께 내려준다. 삭제된 파일은 목록에서 빠진다.")
    @ApiErrorCode(type = NoticeErrorCode.class, codes = {"NOTICE_NOT_FOUND"})
    ApiResponse<AdminNoticeDetailResponse> getNotice(AdminApiUser apiUser, Long noticeId);

    /** 공지 작성. 작성자는 토큰의 관리자로 정해진다. */
    @Operation(summary = "공지 작성",
        description = "공지를 등록한다. 이미지·첨부 파일은 업로드를 마친 파일 ID로 넘기며, 존재하지 않는 파일이 하나라도 있으면 "
            + "등록하지 않는다. 고정 여부(pinned)도 함께 정한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = FileErrorCode.class, codes = {"FILE_NOT_FOUND"})
    ApiResponse<NoticeCreateResponse> createNotice(AdminApiUser apiUser, NoticeCreateRequest request);

    /** 공지 수정. 요청의 수정 시각이 저장된 값과 다르면 거부한다. */
    @Operation(summary = "공지 수정",
        description = "공지의 제목·본문·종류·이미지·첨부 파일·고정 여부를 통째로 바꾼다. 수정 화면이 공지를 읽은 뒤 다른 관리자가 "
            + "먼저 고쳤다면(수정 시각 불일치) 409로 거부한다. 상세 조회에서 받은 updatedAt을 그대로 보낸다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = FileErrorCode.class, codes = {"FILE_NOT_FOUND"})
    @ApiErrorCode(type = NoticeErrorCode.class, codes = {"NOTICE_NOT_FOUND", "NOTICE_UPDATE_CONFLICT"})
    ApiResponse<NoticeUpdateResponse> updateNotice(AdminApiUser apiUser, Long noticeId, NoticeUpdateRequest request);

    /** 공지 삭제. 소프트 삭제라 학생 앱 목록·상세에서 보이지 않게 된다. */
    @Operation(summary = "공지 삭제",
        description = "공지를 삭제한다. 데이터는 남기고 학생 앱에서만 보이지 않게 한다. 연결된 파일은 지우지 않는다.")
    @ApiErrorCode(type = NoticeErrorCode.class, codes = {"NOTICE_NOT_FOUND"})
    ApiResponse<Void> deleteNotice(AdminApiUser apiUser, Long noticeId);

    /** 공지 상단 고정 설정/해제. */
    @Operation(summary = "공지 고정 설정/해제",
        description = "공지를 목록 맨 위에 고정하거나 고정을 해제한다. 이미 그 상태여도 성공으로 응답한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = NoticeErrorCode.class, codes = {"NOTICE_NOT_FOUND"})
    ApiResponse<Void> changePinned(AdminApiUser apiUser, Long noticeId, NoticePinRequest request);
}
