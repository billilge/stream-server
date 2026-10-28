package kr.ac.kookmin.stream.api.admin.welfare.item.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ReturnPolicy;

/** 물품 등록·수정 둘 다 쓰는 반납 정책 입력. 대여품(RENTAL)에만 의미가 있다. */
public record ReturnPolicyRequest(
    @Schema(description = "대여일부터 최대 대여 가능 일수. 0이면 당일 반납", example = "7")
    @NotNull(message = "최대 대여 일수를 입력해 주세요.")
    @Min(value = 0, message = "최대 대여 일수는 0 이상이어야 합니다.")
    Integer maxRentalDays,

    @Schema(description = "반납 마감 시각", example = "18:00")
    @NotNull(message = "반납 마감 시각을 입력해 주세요.")
    LocalTime returnDeadline
) {

    public ReturnPolicy toDomain() {
        return new ReturnPolicy(maxRentalDays, returnDeadline);
    }
}
