package kr.ac.kookmin.stream.api.admin.welfare.notice;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.welfare.notice.request.NoticeCreateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.notice.request.NoticePinRequest;
import kr.ac.kookmin.stream.api.admin.welfare.notice.request.NoticeUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.notice.response.NoticeCreateResponse;
import kr.ac.kookmin.stream.api.admin.welfare.notice.response.NoticeUpdateResponse;
import kr.ac.kookmin.stream.api.admin.welfare.notice.usecase.AdminNoticeWriteUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/notices")
@RequiredArgsConstructor
public class AdminNoticeController implements AdminNoticeApi {

    private final NoticeService noticeService;
    private final AdminNoticeWriteUseCase adminNoticeWriteUseCase;

    @Override
    @PostMapping
    public ApiResponse<NoticeCreateResponse> createNotice(
        AdminApiUser apiUser,
        @Valid @RequestBody NoticeCreateRequest request
    ) {
        Notice notice = adminNoticeWriteUseCase.create(request.toCommand(apiUser.userId()));
        return ApiResponse.success(NoticeCreateResponse.from(notice));
    }

    @Override
    @PutMapping("/{noticeId}")
    public ApiResponse<NoticeUpdateResponse> updateNotice(
        AdminApiUser apiUser,
        @PathVariable Long noticeId,
        @Valid @RequestBody NoticeUpdateRequest request
    ) {
        Notice notice = adminNoticeWriteUseCase.update(noticeId, request.toCommand());
        return ApiResponse.success(NoticeUpdateResponse.from(notice));
    }

    @Override
    @DeleteMapping("/{noticeId}")
    public ApiResponse<Void> deleteNotice(AdminApiUser apiUser, @PathVariable Long noticeId) {
        noticeService.delete(noticeId);
        return ApiResponse.success();
    }

    @Override
    @PatchMapping("/{noticeId}/pin")
    public ApiResponse<Void> changePinned(
        AdminApiUser apiUser,
        @PathVariable Long noticeId,
        @Valid @RequestBody NoticePinRequest request
    ) {
        noticeService.changePinned(noticeId, request.pinned());
        return ApiResponse.success();
    }
}
