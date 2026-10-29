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
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

// 대여 신청의 재고 차감을 버전 컬럼 없는 낙관적 락으로 보호한다. 바뀐 컬럼만 UPDATE하고(@DynamicUpdate),
// 그 컬럼의 읽은 값을 WHERE에 붙인다(UPDATE items SET count = ? WHERE id = ? AND count = 읽은 값).
// 그 사이 다른 트랜잭션이 count를 바꿨으면 0건이 되어 OptimisticLockingFailureException이 난다. 바뀐 컬럼끼리만
// 비교하므로 이름 수정과 재고 차감처럼 서로 다른 컬럼을 고친 동시 수정은 충돌 없이 둘 다 반영된다.
// 운영진이 SQL로 count를 직접 바꿔도 같은 방식으로 감지된다.
// ⚠️ 같은 영속성 컨텍스트에서 읽고 고칠 때만 동작한다(읽은 값을 1차 캐시가 기억한다). 수정 화면처럼 읽은 요청과
//    저장하는 요청이 다르면, 저장 트랜잭션에서 새로 읽은 값과 비교하므로 그 사이의 변경을 덮어쓴다.
@Entity
@Table(
    name = "items",
    indexes = @Index(name = "idx_items_name", columnList = "name")
)
@DynamicUpdate
@OptimisticLocking(type = OptimisticLockType.DIRTY)
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
