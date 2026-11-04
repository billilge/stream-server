package kr.ac.kookmin.stream.api.admin.internal.home.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 배너 노출 순서 일괄 변경 입력값.
 * <p>
 * 노출이 끝나지 않은 배너 전체를 바뀐 순서대로 보낸다. 일부만 보내 자리를 계산하게 하면 중간에 끼워 넣을 때
 * 나머지가 전부 밀려 경합에 약하다.
 *
 * @param bannerIds 바뀐 순서대로의 배너 식별자 전체
 */
public record AdminHomeBannerOrderUpdateRequest(

    @Schema(description = "바뀐 순서대로의 배너 ID 전체", example = "[3, 1, 2]")
    @NotNull(message = "배너 순서를 입력해 주세요.")
    List<@NotNull(message = "배너 ID는 비워 둘 수 없습니다.") Long> bannerIds
) {
}
