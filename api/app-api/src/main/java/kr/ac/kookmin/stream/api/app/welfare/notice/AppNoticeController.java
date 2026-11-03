package kr.ac.kookmin.stream.api.app.welfare.notice;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.api.app.welfare.notice.request.NoticeListParams;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.CursorSliceResponse;
import kr.ac.kookmin.stream.api.app.welfare.notice.response.NoticeDetailResponse;
import kr.ac.kookmin.stream.api.app.welfare.notice.response.NoticeListItemResponse;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.file.domain.File;
import kr.ac.kookmin.stream.file.service.FileService;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/app/notices")
@RequiredArgsConstructor
public class AppNoticeController implements AppNoticeApi {

    private final NoticeService noticeService;
    private final FileService fileService;

    @Override
    @GetMapping
    public ApiResponse<CursorSliceResponse<NoticeListItemResponse>> getNotices(
            @Valid @ModelAttribute NoticeListParams params
    ) {
        CursorSliceResult<Notice> result = noticeService.getNotices(
            params.toCategory(), params.toCursor(), params.sizeOrDefault());

        List<Long> thumbnailIds = result.content().stream()
                .map(AppNoticeController::firstImageId)
                .filter(id -> id != null)
                .toList();
        Map<Long, File> filesById = fileService.findAllByIdIn(thumbnailIds);

        CursorSliceResponse<NoticeListItemResponse> response = CursorSliceResponse.from(
                result,
                notice -> NoticeListItemResponse.from(notice, filesById)
        );

        return ApiResponse.success(response);
    }

    @Override
    @GetMapping("/{noticeId}")
    public ApiResponse<NoticeDetailResponse> getNotice(@PathVariable("noticeId") Long noticeId) {
        Notice notice = noticeService.getById(noticeId);

        Map<Long, File> imagesById = fileService.findAllByIdIn(
                notice.getImageIds() == null ? List.of() : notice.getImageIds()
        );
        Map<Long, File> attachmentsById = fileService.findAllByIdIn(
                notice.getAttachmentIds() == null ? List.of() : notice.getAttachmentIds()
        );

        return ApiResponse.success(NoticeDetailResponse.from(notice, imagesById, attachmentsById));
    }

    private static Long firstImageId(Notice notice) {
        return notice.getImageIds() == null || notice.getImageIds().isEmpty() ? null : notice.getImageIds().get(0);
    }
}
