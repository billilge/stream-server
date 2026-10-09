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
        return new Notice(id, title, content, category, pinned, createdBy, attachmentIds, imageIds, createdAt, updatedAt);
    }

    // 새 공지는 고정되지 않은 채로 등록한다. 생성·수정 시각은 DB가 채운다
    public static Notice create(NoticeCreateCommand command) {
        return new Notice(
            null,
            command.title(),
            command.content(),
            command.category(),
            false,
            command.createdBy(),
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
    }

    public void changePinned(boolean pinned) {
        this.pinned = pinned;
    }
}
