package kr.ac.kookmin.stream.api.admin.welfare.notice.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.file.domain.File;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;

/** 학생 앱 {@code NoticeDetailResponse}와 같은 모양이다. 공지 상세·수정 화면 둘 다 이 응답으로 채운다. */
public record AdminNoticeDetailResponse(
    Long noticeId,
    String title,
    String content,
    NoticeCategory category,
    boolean pinned,
    List<Image> images,
    List<Attachment> attachments,
    LocalDateTime createdAt
) {

    /**
     * 존재하지 않는(삭제된) fileId는 imagesById/attachmentsById에 키가 없다 — 그런 항목은 목록에서 조용히 제외한다.
     */
    public static AdminNoticeDetailResponse from(
        Notice notice,
        Map<Long, File> imagesById,
        Map<Long, File> attachmentsById
    ) {
        List<Image> images = notice.getImageIds().stream()
            .filter(imagesById::containsKey)
            .map(fileId -> Image.of(fileId, imagesById.get(fileId)))
            .toList();
        List<Attachment> attachments = notice.getAttachmentIds().stream()
            .filter(attachmentsById::containsKey)
            .map(fileId -> Attachment.of(fileId, attachmentsById.get(fileId)))
            .toList();

        return new AdminNoticeDetailResponse(
            notice.getId(),
            notice.getTitle(),
            notice.getContent(),
            notice.getCategory(),
            notice.isPinned(),
            images,
            attachments,
            notice.getCreatedAt()
        );
    }

    public record Image(Long fileId, String fileUrl) {

        public static Image of(Long fileId, File file) {
            return new Image(fileId, StorageUrlBuilder.build(file.getFileKey()));
        }
    }

    public record Attachment(Long fileId, String fileName, String fileUrl) {

        public static Attachment of(Long fileId, File file) {
            return new Attachment(fileId, file.getOriginalName(), StorageUrlBuilder.build(file.getFileKey()));
        }
    }
}
