package kr.ac.kookmin.stream.api.app.welfare.rental.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RentalApplyRequest(
    @NotNull(message = "대여할 물품을 선택해 주세요.")
    Long itemId,

    @NotNull(message = "대여 수량을 입력해 주세요.")
    @Positive(message = "대여 수량은 1개 이상이어야 합니다.")
    Integer count,

    @NotNull(message = "대여 시작 시(hour)를 입력해 주세요.")
    @Min(value = 0, message = "대여 시작 시(hour)는 0~23 사이여야 합니다.")
    @Max(value = 23, message = "대여 시작 시(hour)는 0~23 사이여야 합니다.")
    Integer rentAtHour,

    @NotNull(message = "대여 시작 분(minute)을 입력해 주세요.")
    @Min(value = 0, message = "대여 시작 분(minute)은 0~59 사이여야 합니다.")
    @Max(value = 59, message = "대여 시작 분(minute)은 0~59 사이여야 합니다.")
    Integer rentAtMinute,

    @NotNull(message = "중복 대여 진행 여부를 입력해 주세요.")
    Boolean ignoreDuplicate
) {}
