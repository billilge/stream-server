package kr.ac.kookmin.stream.api.app.welfare.notice.response;

import java.time.LocalDateTime;
import java.util.Map;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.file.domain.File;
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

    public static NoticeListItemResponse from(Notice notice, Map<Long, File> filesById) {
        return new NoticeListItemResponse(
            notice.getId(),
            notice.getTitle(),
            notice.getCategory(),
            notice.getCreatedAt(),
            StorageUrlBuilder.build(notice.getThumbnailImageId(), filesById),
            notice.isPinned()
        );
    }
}
