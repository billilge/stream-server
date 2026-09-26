package kr.ac.kookmin.stream.welfare.domain.rental.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Item {

    private Long id;
    private String name;
    private ItemCategory category;
    private ItemType type;
    private int count;
    private String imageKey;
    /** 대여품(RENTAL)의 반납 정책. 소모품이거나 정책이 채워지지 않았으면 null이다. */
    private ReturnPolicy returnPolicy;

    public static Item of(
        Long id,
        String name,
        ItemCategory category,
        ItemType type,
        int count,
        String imageKey,
        ReturnPolicy returnPolicy
    ) {
        // 소모품은 반납하지 않으므로 저장된 값이 있어도 반납 정책을 갖지 않는다
        ReturnPolicy applicablePolicy = type == ItemType.RENTAL ? returnPolicy : null;
        return new Item(id, name, category, type, count, imageKey, applicablePolicy);
    }
}
