package kr.ac.kookmin.stream.api.app.welfare.feedback;

import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.welfare.feedback.response.FeedbackResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.OpenFeedback;
import kr.ac.kookmin.stream.welfare.domain.feedback.service.OpenFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/feedbacks")
@RequiredArgsConstructor
public class AppFeedbackController implements AppFeedbackApi {

    private final OpenFeedbackService openFeedbackService;

    @Override
    @GetMapping("/{feedbackId}")
    public ApiResponse<FeedbackResponse> getFeedback(AppApiUser apiUser, @PathVariable Long feedbackId) {
        OpenFeedback feedback = openFeedbackService.getById(feedbackId);
        return ApiResponse.success(FeedbackResponse.from(feedback));
    }
}
