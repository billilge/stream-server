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
    List<Image> images,
    List<Attachment> attachments,
    LocalDateTime createdAt
) {

    public static NoticeDetailResponse from(Notice notice) {
        List<Image> images = notice.getImageIds() == null
            ? List.of()
            : notice.getImageIds().stream().map(Image::from).toList();
        List<Attachment> attachments = notice.getAttachmentIds() == null
            ? List.of()
            : notice.getAttachmentIds().stream().map(Attachment::from).toList();

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

    public record Image(Long fileId, String fileUrl) {

        public static Image from(Long fileId) {
            return new Image(fileId, null);
        }
    }

    public record Attachment(Long fileId, String fileName, String fileUrl) {

        public static Attachment from(Long fileId) {
            return new Attachment(fileId, null, null);
        }
    }
}
