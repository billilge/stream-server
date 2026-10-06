package kr.ac.kookmin.stream.db.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.ac.kookmin.stream.db.common.BaseTimeEntity;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionLayout;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "locker_section_layouts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LockerSectionLayoutJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "locker_section_layout_id")
    private Long id;

    @Column(name = "section_id", nullable = false)
    private Long sectionId;

    /** JSON 원문을 문자열 그대로 읽고 쓴다. 블록 구조는 앱이 해석한다. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "json")
    private String layout;

    /**
     * layout 형식 버전. 낙관적 락용 {@code @Version}이 아니다.
     * 컬럼이 SMALLINT라 스키마 검증이 INTEGER를 기대하지 않도록 JDBC 타입을 지정한다.
     */
    @JdbcTypeCode(SqlTypes.SMALLINT)
    @Column(nullable = false)
    private int version;

    @Column(name = "photo_file_id", nullable = false)
    private Long photoFileId;

    private LockerSectionLayoutJpaEntity(LockerSectionLayout sectionLayout) {
        this.id = sectionLayout.getId();
        this.sectionId = sectionLayout.getSectionId();
        this.layout = sectionLayout.getLayout();
        this.version = sectionLayout.getVersion();
        this.photoFileId = sectionLayout.getPhotoFileId();
    }

    public static LockerSectionLayoutJpaEntity from(LockerSectionLayout sectionLayout) {
        return new LockerSectionLayoutJpaEntity(sectionLayout);
    }

    public LockerSectionLayout toDomain() {
        return LockerSectionLayout.of(id, sectionId, layout, version, photoFileId);
    }
}
