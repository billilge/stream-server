package kr.ac.kookmin.stream.db.welfare;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.ac.kookmin.stream.db.common.BaseTimeEntity;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ItemType type;

    @Column(nullable = false)
    private int count;

    @Column(name = "image_key")
    private String imageKey;

    private ItemJpaEntity(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.type = item.getType();
        this.count = item.getCount();
        this.imageKey = item.getImageKey();
    }

    public static ItemJpaEntity from(Item item) {
        return new ItemJpaEntity(item);
    }

    public Item toDomain() {
        return Item.of(id, name, type, count, imageKey);
    }
}
