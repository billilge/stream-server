package kr.ac.kookmin.stream.db.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.ac.kookmin.stream.db.common.BaseTimeEntity;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "locker_sections")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LockerSectionJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "locker_section_id")
    private Long id;

    @Column(nullable = false, length = 20)
    private String label;

    private LockerSectionJpaEntity(LockerSection section) {
        this.id = section.getId();
        this.label = section.getLabel();
    }

    public static LockerSectionJpaEntity from(LockerSection section) {
        return new LockerSectionJpaEntity(section);
    }

    public LockerSection toDomain() {
        return LockerSection.of(id, label);
    }
}
