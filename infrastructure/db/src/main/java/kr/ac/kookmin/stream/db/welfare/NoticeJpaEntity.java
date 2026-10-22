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
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.type.SqlTypes;

// 공지 수정·삭제가 겹쳐도 앞선 변경을 덮어쓰지 않도록 버전 컬럼 없는 낙관적 락으로 보호한다. 바뀐 컬럼만 UPDATE하고
// (@DynamicUpdate), 그 컬럼의 읽은 값을 WHERE에 붙인다(UPDATE notices SET title = ? WHERE notice_id = ? AND title = 읽은 값).
// 읽은 뒤 UPDATE 전에 다른 트랜잭션이 같은 컬럼을 바꿨으면 0건이 되어 OptimisticLockingFailureException이 난다.
// 서로 다른 컬럼을 고친 동시 수정은 충돌 없이 둘 다 반영된다.
// ⚠️ 이 락은 같은 트랜잭션에서 읽은 값과 비교하므로 수정 화면이 공지를 읽은 뒤 다른 관리자가 고쳤는지는 알 수 없다.
//    그 검사는 서비스가 요청의 updatedAt과 비교해 맡고, 이 락은 그 검사 뒤 UPDATE 직전의 틈만 닫는다.
// ⚠️ 읽기와 저장이 한 트랜잭션이어야 읽은 값이 WHERE에 쓰인다. 공지 수정의 @Transactional을 빼면 이 락은 동작하지 않는다.
@Entity
@Table(
    name = "notices",
    indexes = {
        @Index(name = "idx_notices_is_deleted_pinned_created_at", columnList = "is_deleted, pinned, created_at")
    }
)
@DynamicUpdate
@OptimisticLocking(type = OptimisticLockType.DIRTY)
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
