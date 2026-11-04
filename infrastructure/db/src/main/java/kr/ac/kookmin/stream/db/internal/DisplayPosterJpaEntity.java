package kr.ac.kookmin.stream.db.internal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.ac.kookmin.stream.db.common.BaseCreatedTimeEntity;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayPoster;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "display_posters")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DisplayPosterJpaEntity extends BaseCreatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "image_file_id", nullable = false)
    private Long imageFileId;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    private DisplayPosterJpaEntity(DisplayPoster poster) {
        this.id = poster.getId();
        this.title = poster.getTitle();
        this.imageFileId = poster.getImageFileId();
        this.active = poster.isActive();
    }

    public static DisplayPosterJpaEntity from(DisplayPoster poster) {
        return new DisplayPosterJpaEntity(poster);
    }

    public DisplayPoster toDomain() {
        return DisplayPoster.of(id, title, imageFileId, active, getCreatedAt());
    }
}
