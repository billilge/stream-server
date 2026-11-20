package kr.ac.kookmin.stream.api.ai.welfare.feedback;

import java.util.List;
import kr.ac.kookmin.stream.api.ai.welfare.feedback.response.AiFeedbackResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.Feedback;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackPeriod;
import kr.ac.kookmin.stream.welfare.domain.feedback.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/ai/feedbacks")
@RequiredArgsConstructor
public class AiFeedbackController implements AiFeedbackApi {

    private static final int PAGE_SIZE = 100;

    // 연도·회차를 정하지 않으면 공개된 모든 회차다
    private static final FeedbackPeriod ALL_PERIODS = FeedbackPeriod.of(null, null);

    private final FeedbackService feedbackService;

    /**
     * 학생 열린피드백 목록과 같은 서비스다. 답변이 공개된 회차의, 답변된 질문만 나온다.
     */
    @Override
    @GetMapping
    public ApiResponse<List<AiFeedbackResponse>> getFeedbacks() {
        List<Feedback> feedbacks = PageResult.fetchAll(
            PAGE_SIZE, offset -> feedbackService.search(ALL_PERIODS, null, offset));

        return ApiResponse.success(feedbacks.stream().map(AiFeedbackResponse::from).toList());
    }
}
