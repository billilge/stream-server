package kr.ac.kookmin.stream.welfare.domain.rental.domain;

import java.time.LocalDateTime;

/**
 * 반납해야 하는 대여 한 건. 대여 시각과 물품의 반납 정책으로 계산한 반납 기한을 함께 담는다.
 */
public record ReturnRequiredRental(
    Long historyId,
    String itemName,
    String itemImageKey,
    LocalDateTime dueAt
) {

    /**
     * @param history 대여 시각(rentAt)이 있는 이력
     * @param item    반납 정책이 있는 물품
     */
    public static ReturnRequiredRental of(RentalHistory history, Item item) {
        return new ReturnRequiredRental(
            history.getId(),
            item.getName(),
            item.getImageKey(),
            item.getReturnPolicy().dueAt(history.getRentAt())
        );
    }
}
