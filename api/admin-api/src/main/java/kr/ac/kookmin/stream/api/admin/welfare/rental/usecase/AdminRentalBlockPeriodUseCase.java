package kr.ac.kookmin.stream.api.admin.welfare.rental.usecase;

import java.time.LocalDate;
import java.util.Map;
import kr.ac.kookmin.stream.api.admin.welfare.rental.request.RentalBlockPeriodUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.rental.response.RentalBlockPeriodResponse;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.internal.domain.config.service.AdminConfigValueService;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalBlockPeriodConfigKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 대여 이용 제한 기간 조회·설정. admin_config_values key-value를 그대로 쓴다. */
@Component
@RequiredArgsConstructor
public class AdminRentalBlockPeriodUseCase {

    private final AdminConfigValueService adminConfigValueService;

    public RentalBlockPeriodResponse getCurrent() {
        LocalDate startDate = readDate(RentalBlockPeriodConfigKeys.RENTAL_BLOCK_START);
        LocalDate endDate = readDate(RentalBlockPeriodConfigKeys.RENTAL_BLOCK_END);
        String reason = adminConfigValueService.getValue(RentalBlockPeriodConfigKeys.RENTAL_BLOCK_REASON).orElse(null);
        return RentalBlockPeriodResponse.of(startDate, endDate, reason);
    }

    public RentalBlockPeriodResponse update(RentalBlockPeriodUpdateRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT);
        }

        adminConfigValueService.upsertValues(Map.of(
            RentalBlockPeriodConfigKeys.RENTAL_BLOCK_START, request.startDate().toString(),
            RentalBlockPeriodConfigKeys.RENTAL_BLOCK_END, request.endDate().toString(),
            RentalBlockPeriodConfigKeys.RENTAL_BLOCK_REASON, request.reason()
        ));

        return RentalBlockPeriodResponse.of(request.startDate(), request.endDate(), request.reason());
    }

    private LocalDate readDate(String configKey) {
        return adminConfigValueService.getValue(configKey).map(LocalDate::parse).orElse(null);
    }
}
