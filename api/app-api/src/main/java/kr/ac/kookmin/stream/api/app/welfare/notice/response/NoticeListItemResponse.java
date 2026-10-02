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

    /** 등록된 이미지 중 첫 번째를 썸네일로 쓴다. */
    public static NoticeListItemResponse from(Notice notice, Map<Long, File> filesById) {
        Long thumbnailId = notice.getImageIds() == null || notice.getImageIds().isEmpty()
            ? null
            : notice.getImageIds().get(0);

        return new NoticeListItemResponse(
            notice.getId(),
            notice.getTitle(),
            notice.getCategory(),
            notice.getCreatedAt(),
            StorageUrlBuilder.build(thumbnailId, filesById),
            notice.isPinned()
        );
    }
}
