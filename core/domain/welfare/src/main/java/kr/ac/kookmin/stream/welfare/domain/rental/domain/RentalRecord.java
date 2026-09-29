package kr.ac.kookmin.stream.welfare.domain.rental.domain;

import java.time.LocalDateTime;

/**
 * 대여 이력에 그 물품을 붙인 조회용 조합 객체. 필드를 복사하지 않고 도메인 객체를 그대로 담아,
 * 화면이 어떤 필드를 쓰는지는 응답(api)이 고른다.
 * <p>
 * 물품 참조는 DB가 아닌 애플리케이션이 관리하므로 {@code item}은 사라진 물품이면 null이다.
 */
public record RentalRecord(RentalHistory history, Item item) {

    /**
     * 지금 반납해야 하는 대여인지. 대여 중이고, 대여 시각이 있고, 반납 정책이 있는 물품이어야 기한을 계산할 수 있다.
     */
    public boolean isReturnRequired() {
        return history.getRentalStatus() == RentalStatus.RENTAL
            && history.getRentAt() != null
            && item != null
            && item.getReturnPolicy() != null;
    }

    /**
     * 반납 기한 시각. {@link #isReturnRequired()}가 true일 때만 계산할 수 있다.
     */
    public LocalDateTime dueAt() {
        return item.getReturnPolicy().dueAt(history.getRentAt());
    }

    public String itemName() {
        return item == null ? null : item.getName();
    }

    public String itemImageKey() {
        return item == null ? null : item.getImageKey();
    }
}
