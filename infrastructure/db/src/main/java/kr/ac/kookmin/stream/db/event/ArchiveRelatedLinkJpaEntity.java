package kr.ac.kookmin.stream.db.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.ac.kookmin.stream.db.common.BaseTimeEntity;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveRelatedLink;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "archive_related_links")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArchiveRelatedLinkJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "archive_related_link_id")
    private Long id;

    @Column(name = "archive_id", nullable = false)
    private Long archiveId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 2048)
    private String url;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    private ArchiveRelatedLinkJpaEntity(ArchiveRelatedLink link) {
        this.id = link.getId();
        this.archiveId = link.getArchiveId();
        this.title = link.getTitle();
        this.url = link.getUrl();
        this.displayOrder = link.getDisplayOrder();
    }

    public static ArchiveRelatedLinkJpaEntity from(ArchiveRelatedLink link) {
        return new ArchiveRelatedLinkJpaEntity(link);
    }

    public ArchiveRelatedLink toDomain() {
        return ArchiveRelatedLink.of(id, archiveId, title, url, displayOrder);
    }
}
