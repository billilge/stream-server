package kr.ac.kookmin.stream.welfare.domain.notice.domain;

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

    public static Notice of(
        Long id,
        String title,
        String content,
        NoticeCategory category,
        boolean pinned,
        Long createdBy,
        List<Long> attachmentIds,
        List<Long> imageIds
    ) {
        return new Notice(id, title, content, category, pinned, createdBy, attachmentIds, imageIds);
    }
}
