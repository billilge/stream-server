package kr.ac.kookmin.stream.api.app.welfare.notice;

import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.CursorCodec;
import kr.ac.kookmin.stream.api.common.dto.CursorSliceResponse;
import kr.ac.kookmin.stream.api.app.welfare.notice.response.NoticeDetailResponse;
import kr.ac.kookmin.stream.api.app.welfare.notice.response.NoticeListItemResponse;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCursor;
import kr.ac.kookmin.stream.welfare.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/app/notices")
@RequiredArgsConstructor
public class AppNoticeController implements AppNoticeApi {

    private final NoticeService noticeService;

    @Override
    @GetMapping
    public ApiResponse<CursorSliceResponse<NoticeListItemResponse>> getNotices(
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "cursor", required = false) String cursor,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        NoticeCursor noticeCursor = cursor == null ? null : NoticeCursor.from(CursorCodec.decode(cursor));
        CursorSliceResult<Notice> result = noticeService.getNotices(NoticeCategory.from(category), noticeCursor, size);
        CursorSliceResponse<NoticeListItemResponse> response = CursorSliceResponse.from(
                result,
                NoticeListItemResponse::from
        );

        return ApiResponse.success(response);
    }

    @Override
    @GetMapping("/{noticeId}")
    public ApiResponse<NoticeDetailResponse> getNotice(@PathVariable("noticeId") Long noticeId) {
        Notice notice = noticeService.getNotice(noticeId);

        return ApiResponse.success(NoticeDetailResponse.from(notice));
    }
}
