package kr.ac.kookmin.stream.db.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
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

    /** layout 형식 버전. 낙관적 락용 {@code @Version}이 아니다. */
    @Column(nullable = false, columnDefinition = "SMALLINT")
    private int version;

    /** 구역 실제 사진. 배열 순서가 노출 순서다. 파일 목록을 JSON으로 두는 건 notices.image_ids와 같은 방식이다. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "photo_file_ids", columnDefinition = "json")
    private List<Long> photoFileIds;

    private LockerSectionLayoutJpaEntity(LockerSectionLayout sectionLayout) {
        this.id = sectionLayout.getId();
        this.sectionId = sectionLayout.getSectionId();
        this.layout = sectionLayout.getLayout();
        this.version = sectionLayout.getVersion();
        this.photoFileIds = sectionLayout.getPhotoFileIds();
    }

    public static LockerSectionLayoutJpaEntity from(LockerSectionLayout sectionLayout) {
        return new LockerSectionLayoutJpaEntity(sectionLayout);
    }

    public LockerSectionLayout toDomain() {
        // 사진을 한 장도 등록하지 않은 구역은 컬럼이 NULL이다. 읽는 쪽이 매번 null을 보지 않도록 빈 목록으로 바꾼다
        return LockerSectionLayout.of(id, sectionId, layout, version, photoFileIds == null ? List.of() : photoFileIds);
    }
}
