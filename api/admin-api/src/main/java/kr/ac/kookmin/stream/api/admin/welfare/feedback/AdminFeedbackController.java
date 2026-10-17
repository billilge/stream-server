package kr.ac.kookmin.stream.api.admin.welfare.feedback;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.welfare.feedback.response.AdminFeedbackResponse;
import kr.ac.kookmin.stream.api.admin.welfare.feedback.usecase.AdminFeedbackSearchUseCase;
import kr.ac.kookmin.stream.api.admin.welfare.feedback.usecase.FeedbackWithStudent;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.common.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/feedbacks")
@RequiredArgsConstructor
public class AdminFeedbackController implements AdminFeedbackApi {

    private final AdminFeedbackSearchUseCase adminFeedbackSearchUseCase;

    @Override
    @GetMapping
    public ApiResponse<PageResponse<AdminFeedbackResponse>> getFeedbacks(
        AdminApiUser apiUser,
        @RequestParam(required = false) Long roundId,
        @RequestParam(required = false) String keyword,
        @Valid @ModelAttribute PageParams pageParams
    ) {
        PageResult<FeedbackWithStudent> result =
            adminFeedbackSearchUseCase.search(roundId, keyword, pageParams.toOffset());

        return ApiResponse.success(PageResponse.from(result, AdminFeedbackResponse::from));
    }
}
