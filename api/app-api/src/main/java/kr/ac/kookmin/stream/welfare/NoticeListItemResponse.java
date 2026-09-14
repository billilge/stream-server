package kr.ac.kookmin.stream.welfare;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;

public record NoticeListItemResponse(
    Long noticeId,
    String title,
    NoticeCategory category,
    LocalDateTime createdAt,
    String thumbnailUrl,
    boolean pinned
) {

    public static NoticeListItemResponse from(Notice notice) {
        return new NoticeListItemResponse(
            notice.getId(),
            notice.getTitle(),
            notice.getCategory(),
            notice.getCreatedAt(),
            null,
            notice.isPinned()
        );
    }
}
