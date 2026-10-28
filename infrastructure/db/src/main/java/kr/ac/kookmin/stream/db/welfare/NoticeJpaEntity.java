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
import java.util.List;
import kr.ac.kookmin.stream.db.common.BaseSoftDeleteEntity;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

// 바뀐 컬럼만 UPDATE한다(@DynamicUpdate). 수정과 삭제가 겹쳤을 때 읽어 둔 나머지 컬럼까지 되써서 삭제 표시(is_deleted)나
// 다른 관리자가 방금 고친 값을 되돌리는 일을 막는다. 동시 수정을 막는 락은 두지 않아 겹치면 나중에 저장한 내용이 반영된다.
@Entity
@Table(
    name = "notices",
    indexes = {
        @Index(name = "idx_notices_is_deleted_pinned_created_at", columnList = "is_deleted, pinned, created_at")
    }
)
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeJpaEntity extends BaseSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NoticeCategory category;

    @Column(nullable = false)
    private boolean pinned;

    @Column(name = "created_by")
    private Long createdBy;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attachment_ids", columnDefinition = "json")
    private List<Long> attachmentIds;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "image_ids", columnDefinition = "json")
    private List<Long> imageIds;

    private NoticeJpaEntity(Notice notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.category = notice.getCategory();
        this.pinned = notice.isPinned();
        this.createdBy = notice.getCreatedBy();
        this.attachmentIds = notice.getAttachmentIds();
        this.imageIds = notice.getImageIds();
    }

    public static NoticeJpaEntity from(Notice notice) {
        return new NoticeJpaEntity(notice);
    }

    public Notice toDomain() {
        return Notice.of(
            id, title, content, category, pinned, createdBy, attachmentIds, imageIds, getCreatedAt(), getUpdatedAt());
    }
}
