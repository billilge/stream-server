package kr.ac.kookmin.stream.api.admin.internal.home.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplyTargetType;

/**
 * 홈 신청 섹션 후보 한 행. 제목과 신청 기간은 저장된 값이 아니라 행사·회차에서 읽어 온 것이다.
 * <p>
 * 노출 여부를 바꾸거나 순서를 바꿀 때 {@code targetType}·{@code targetId}를 그대로 돌려보낸다.
 */
public record AdminHomeApplySectionListItemResponse(
    HomeApplyTargetType targetType,
    Long targetId,
    String title,
    LocalDateTime applyStartAt,
    LocalDateTime applyEndAt,
    boolean isExposed
) {
}
