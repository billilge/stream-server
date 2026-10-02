package kr.ac.kookmin.stream.db.welfare;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalTime;
import kr.ac.kookmin.stream.db.common.BaseTimeEntity;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCategory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemType;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ReturnPolicy;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "items",
    indexes = @Index(name = "idx_items_name", columnList = "name")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ItemCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ItemType type;

    @Column(nullable = false)
    private int count;

    @Column(name = "image_key")
    private String imageKey;

    @Column(name = "max_rental_days")
    private Integer maxRentalDays;

    @Column(name = "return_deadline")
    private LocalTime returnDeadline;

    private ItemJpaEntity(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.category = item.getCategory();
        this.type = item.getType();
        this.count = item.getCount();
        this.imageKey = item.getImageKey();
        ReturnPolicy returnPolicy = item.getReturnPolicy();
        this.maxRentalDays = returnPolicy == null ? null : returnPolicy.maxRentalDays();
        this.returnDeadline = returnPolicy == null ? null : returnPolicy.returnDeadline();
    }

    public static ItemJpaEntity from(Item item) {
        return new ItemJpaEntity(item);
    }

    public Item toDomain() {
        // 두 값이 모두 채워져야 정책이 성립한다
        ReturnPolicy returnPolicy = maxRentalDays == null || returnDeadline == null
            ? null
            : new ReturnPolicy(maxRentalDays, returnDeadline);
        return Item.of(id, name, category, type, count, imageKey, returnPolicy);
    }
}
