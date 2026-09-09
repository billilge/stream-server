package kr.ac.kookmin.stream.welfare;

import kr.ac.kookmin.stream.ApiResponse;
import kr.ac.kookmin.stream.CursorSliceResponse;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;
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
public class AppNoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public ApiResponse<CursorSliceResponse<NoticeListItemResponse>> getNotices(
        @RequestParam(name = "category", required = false) String category,
        @RequestParam(name = "cursor", required = false) String cursor,
        @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        CursorSliceResult<Notice> result = noticeService.getNotices(parseCategory(category), cursor, size);
        CursorSliceResponse<NoticeListItemResponse> response = new CursorSliceResponse<>(
            result.content().stream().map(NoticeListItemResponse::from).toList(),
            result.hasNext(),
            result.nextCursor()
        );
        return ApiResponse.success(response);
    }

    @GetMapping("/{noticeId}")
    public ApiResponse<NoticeDetailResponse> getNotice(@PathVariable("noticeId") Long noticeId) {
        Notice notice = noticeService.getNotice(noticeId);
        return ApiResponse.success(NoticeDetailResponse.from(notice));
    }

    private NoticeCategory parseCategory(String category) {
        if (category == null) {
            return null;
        }
        try {
            return NoticeCategory.valueOf(category);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT);
        }
    }
}
