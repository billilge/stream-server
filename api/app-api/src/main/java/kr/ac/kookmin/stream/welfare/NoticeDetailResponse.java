package kr.ac.kookmin.stream.welfare;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;

public record NoticeDetailResponse(
    Long noticeId,
    String title,
    String content,
    NoticeCategory category,
    List<NoticeImageResponse> images,
    List<NoticeAttachmentResponse> attachments,
    LocalDateTime createdAt
) {

    public static NoticeDetailResponse from(Notice notice) {
        List<NoticeImageResponse> images = notice.getImageIds() == null
            ? List.of()
            : notice.getImageIds().stream().map(NoticeImageResponse::from).toList();
        List<NoticeAttachmentResponse> attachments = notice.getAttachmentIds() == null
            ? List.of()
            : notice.getAttachmentIds().stream().map(NoticeAttachmentResponse::from).toList();

        return new NoticeDetailResponse(
            notice.getId(),
            notice.getTitle(),
            notice.getContent(),
            notice.getCategory(),
            images,
            attachments,
            notice.getCreatedAt()
        );
    }
}
