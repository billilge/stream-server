package kr.ac.kookmin.stream.welfare.domain.rental.domain;

import java.time.LocalDateTime;

/**
 * 내 대여 이력 목록 한 건. 이력에 물품 이름·이미지를 붙인 읽기 모델이다.
 * 물품이 없으면(참조 무결성은 DB가 아닌 애플리케이션이 관리한다) 이름·이미지는 null이다.
 */
public record RentalHistorySummary(
    Long historyId,
    String itemName,
    String itemImageKey,
    LocalDateTime rentAt,
    LocalDateTime returnedAt,
    RentalStatus status
) {

    /**
     * @param item 이력의 물품. 물품이 사라졌으면 {@code null}
     */
    public static RentalHistorySummary of(RentalHistory history, Item item) {
        return new RentalHistorySummary(
            history.getId(),
            item == null ? null : item.getName(),
            item == null ? null : item.getImageKey(),
            history.getRentAt(),
            history.getReturnedAt(),
            history.getRentalStatus()
        );
    }
}
