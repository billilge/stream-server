package kr.ac.kookmin.stream.api.admin.welfare.feedback;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.welfare.feedback.request.FeedbackAnswerSaveRequest;
import kr.ac.kookmin.stream.api.admin.welfare.feedback.request.FeedbackRoundCreateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.feedback.response.FeedbackRoundDetailResponse;
import kr.ac.kookmin.stream.api.admin.welfare.feedback.response.FeedbackRoundResponse;
import kr.ac.kookmin.stream.api.admin.welfare.feedback.usecase.AdminFeedbackAnswerUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRoundStatus;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRoundSummary;
import kr.ac.kookmin.stream.welfare.domain.feedback.service.AdminFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/feedback-rounds")
@RequiredArgsConstructor
public class AdminFeedbackRoundController implements AdminFeedbackRoundApi {

    private final AdminFeedbackService adminFeedbackService;
    private final AdminFeedbackAnswerUseCase adminFeedbackAnswerUseCase;

    @Override
    @GetMapping("/current")
    public ApiResponse<FeedbackRoundResponse> getCurrentRound(AdminApiUser apiUser) {
        // 진행 중 회차가 없으면 data를 null로 내려 화면이 "진행 중인 회차 없음"을 그린다
        return ApiResponse.success(
            adminFeedbackService.getCurrentRound().map(FeedbackRoundResponse::from).orElse(null));
    }

    @Override
    @GetMapping
    public ApiResponse<PageResponse<FeedbackRoundResponse>> getRounds(
        AdminApiUser apiUser,
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) String status,
        @Valid @ModelAttribute PageParams pageParams
    ) {
        FeedbackRoundStatus roundStatus = FeedbackRoundStatus.from(status);
        PageResult<FeedbackRoundSummary> result =
            adminFeedbackService.getRounds(year, roundStatus, pageParams.toOffset());

        return ApiResponse.success(PageResponse.from(result, FeedbackRoundResponse::from));
    }

    @Override
    @PostMapping
    public ApiResponse<FeedbackRoundResponse> createRound(
        AdminApiUser apiUser,
        @Valid @RequestBody FeedbackRoundCreateRequest request
    ) {
        FeedbackRoundSummary created = adminFeedbackService.createRound(request.opensOn(), request.closesOn());
        return ApiResponse.success(FeedbackRoundResponse.from(created));
    }

    @Override
    @GetMapping("/{roundId}")
    public ApiResponse<FeedbackRoundDetailResponse> getRoundAnswers(
        AdminApiUser apiUser,
        @PathVariable Long roundId
    ) {
        return ApiResponse.success(
            FeedbackRoundDetailResponse.from(adminFeedbackAnswerUseCase.getRoundAnswers(roundId)));
    }

    @Override
    @PatchMapping("/{roundId}/answers")
    public ApiResponse<FeedbackRoundDetailResponse> saveAnswers(
        AdminApiUser apiUser,
        @PathVariable Long roundId,
        @Valid @RequestBody FeedbackAnswerSaveRequest request
    ) {
        return ApiResponse.success(FeedbackRoundDetailResponse.from(
            adminFeedbackAnswerUseCase.saveAnswers(roundId, request.toCommands(), apiUser.userId())));
    }

    @Override
    @PostMapping("/{roundId}/answers/publish")
    public ApiResponse<FeedbackRoundDetailResponse> publishAnswers(
        AdminApiUser apiUser,
        @PathVariable Long roundId
    ) {
        return ApiResponse.success(
            FeedbackRoundDetailResponse.from(adminFeedbackAnswerUseCase.publishAnswers(roundId)));
    }
}
