package kr.ac.kookmin.stream.api.app.welfare.notice.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.file.domain.FileInfo;
import kr.ac.kookmin.stream.file.domain.FileUrl;
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

    /**
     * 존재하지 않는(삭제된) fileId는 imageUrls/attachmentInfos에 키가 없다 — 그런 항목은 목록에서 조용히 제외한다.
     */
    public static NoticeDetailResponse from(Notice notice, Map<Long, FileUrl> imageUrls, Map<Long, FileInfo> attachmentInfos) {
        List<Image> images = notice.getImageIds() == null
            ? List.of()
            : notice.getImageIds().stream()
                .filter(imageUrls::containsKey)
                .map(fileId -> Image.of(fileId, imageUrls.get(fileId)))
                .toList();
        List<Attachment> attachments = notice.getAttachmentIds() == null
            ? List.of()
            : notice.getAttachmentIds().stream()
                .filter(attachmentInfos::containsKey)
                .map(fileId -> Attachment.of(fileId, attachmentInfos.get(fileId)))
                .toList();

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

        public static Image of(Long fileId, FileUrl fileUrl) {
            return new Image(fileId, fileUrl.url());
        }
    }

    public record Attachment(Long fileId, String fileName, String fileUrl) {

        public static Attachment of(Long fileId, FileInfo info) {
            return new Attachment(fileId, info.originalName(), info.url().url());
        }
    }
}
