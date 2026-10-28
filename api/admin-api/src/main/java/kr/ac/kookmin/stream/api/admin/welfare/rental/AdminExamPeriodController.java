package kr.ac.kookmin.stream.api.admin.welfare.rental;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Map;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.welfare.rental.request.ExamPeriodUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.rental.response.ExamPeriodResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.internal.domain.config.service.AdminConfigValueService;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ExamPeriodConfigKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/billilge/examperiod")
@RequiredArgsConstructor
public class AdminExamPeriodController implements AdminExamPeriodApi {

    private final AdminConfigValueService adminConfigValueService;

    @Override
    @GetMapping
    public ApiResponse<ExamPeriodResponse> getExamPeriod(AdminApiUser apiUser) {
        LocalDate startDate = readDate(ExamPeriodConfigKeys.EXAM_PERIOD_START);
        LocalDate endDate = readDate(ExamPeriodConfigKeys.EXAM_PERIOD_END);
        return ApiResponse.success(ExamPeriodResponse.of(startDate, endDate));
    }

    @Override
    @PutMapping
    public ApiResponse<ExamPeriodResponse> updateExamPeriod(
        AdminApiUser apiUser,
        @Valid @RequestBody ExamPeriodUpdateRequest request
    ) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT);
        }

        adminConfigValueService.upsertValues(Map.of(
            ExamPeriodConfigKeys.EXAM_PERIOD_START, request.startDate().toString(),
            ExamPeriodConfigKeys.EXAM_PERIOD_END, request.endDate().toString()
        ));

        return ApiResponse.success(ExamPeriodResponse.of(request.startDate(), request.endDate()));
    }

    private LocalDate readDate(String configKey) {
        return adminConfigValueService.getValue(configKey).map(LocalDate::parse).orElse(null);
    }
}
