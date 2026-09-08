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
    private ItemType type;
    private int count;
    private String imageKey;

    public static Item of(Long id, String name, ItemType type, int count, String imageKey) {
        return new Item(id, name, type, count, imageKey);
    }
}
