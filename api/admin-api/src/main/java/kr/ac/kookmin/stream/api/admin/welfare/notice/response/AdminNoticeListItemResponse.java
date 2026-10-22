package kr.ac.kookmin.stream.api.admin.welfare.notice.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;

public record AdminNoticeListItemResponse(
    Long noticeId,
    String title,
    NoticeCategory category,
    boolean pinned,
    LocalDateTime createdAt
) {

    public static AdminNoticeListItemResponse from(Notice notice) {
        return new AdminNoticeListItemResponse(
            notice.getId(),
            notice.getTitle(),
            notice.getCategory(),
            notice.isPinned(),
            notice.getCreatedAt()
        );
    }
}
