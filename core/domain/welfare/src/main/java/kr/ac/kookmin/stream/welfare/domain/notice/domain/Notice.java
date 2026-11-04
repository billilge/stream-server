package kr.ac.kookmin.stream.welfare.domain.notice.domain;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Notice {

    private Long id;
    private String title;
    private String content;
    private NoticeCategory category;
    private boolean pinned;
    private Long createdBy;
    private List<Long> attachmentIds;
    private List<Long> imageIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Notice of(
        Long id,
        String title,
        String content,
        NoticeCategory category,
        boolean pinned,
        Long createdBy,
        List<Long> attachmentIds,
        List<Long> imageIds,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        return new Notice(
            id, title, content, category, pinned, createdBy,
            attachmentIds == null ? List.of() : attachmentIds,
            imageIds == null ? List.of() : imageIds,
            createdAt, updatedAt
        );
    }

    // 생성·수정 시각은 DB가 채운다
    public static Notice create(Long createdBy, NoticeCreateCommand command) {
        return new Notice(
            null,
            command.title(),
            command.content(),
            command.category(),
            command.pinned(),
            createdBy,
            command.attachmentIds(),
            command.imageIds(),
            null,
            null
        );
    }

    public void update(NoticeUpdateCommand command) {
        this.title = command.title();
        this.content = command.content();
        this.category = command.category();
        this.attachmentIds = command.attachmentIds();
        this.imageIds = command.imageIds();
        this.pinned = command.pinned();
    }

    /** 등록된 이미지 중 첫 번째를 썸네일로 쓴다. 이미지가 없으면 null이다. */
    public Long getThumbnailImageId() {
        return imageIds.isEmpty() ? null : imageIds.get(0);
    }
}
