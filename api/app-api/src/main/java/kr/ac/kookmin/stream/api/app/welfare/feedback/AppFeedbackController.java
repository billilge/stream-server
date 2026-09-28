package kr.ac.kookmin.stream.api.app.welfare.feedback;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.welfare.feedback.request.FeedbackCreateRequest;
import kr.ac.kookmin.stream.api.app.welfare.feedback.response.FeedbackResponse;
import kr.ac.kookmin.stream.api.app.welfare.feedback.response.FeedbackRoundsResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.Feedback;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRoundOptions;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackPeriod;
import kr.ac.kookmin.stream.welfare.domain.feedback.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/app/feedbacks")
@RequiredArgsConstructor
public class AppFeedbackController implements AppFeedbackApi {

    private final FeedbackService feedbackService;

    @Override
    @GetMapping("/{feedbackId}")
    public ApiResponse<FeedbackResponse> getFeedback(AppApiUser apiUser, @PathVariable Long feedbackId) {
        Feedback feedback = feedbackService.getById(feedbackId);
        return ApiResponse.success(FeedbackResponse.from(feedback));
    }

    @Override
    @GetMapping
    public ApiResponse<PageResponse<FeedbackResponse>> getFeedbacks(
        AppApiUser apiUser,
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) Integer round,
        @Valid @ModelAttribute PageParams pageParams
    ) {
        FeedbackPeriod period = FeedbackPeriod.of(year, round);
        PageResult<Feedback> result = feedbackService.search(period, pageParams.toOffset());
        return ApiResponse.success(PageResponse.from(result, FeedbackResponse::from));
    }

    @Override
    @PostMapping
    public ApiResponse<FeedbackResponse> createFeedback(
        AppApiUser apiUser,
        @Valid @RequestBody FeedbackCreateRequest request
    ) {
        Feedback feedback = feedbackService.create(apiUser.userId(), request.question());
        return ApiResponse.success(FeedbackResponse.from(feedback));
    }

    @Override
    @GetMapping("/rounds")
    public ApiResponse<FeedbackRoundsResponse> getFeedbackRounds(
        AppApiUser apiUser,
        @RequestParam(required = false) Integer year
    ) {
        FeedbackRoundOptions options = feedbackService.getRoundOptions(year);
        return ApiResponse.success(FeedbackRoundsResponse.from(options));
    }
}
