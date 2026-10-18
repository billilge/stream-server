package kr.ac.kookmin.stream.api.admin.welfare.notice.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.api.admin.welfare.notice.usecase.AdminNoticeDetail;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.common.DateUtil;
import kr.ac.kookmin.stream.file.domain.File;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;

/**
 * @param images      수정할 때 {@code imageFileIds}로 되돌려 보낼 수 있도록 파일 ID를 함께 내려준다. 저장된 순서 그대로이고 첫 번째가 썸네일이다
 * @param updatedAt   수정할 때 그대로 되돌려 보내는 수정 시각(KST 기준 epoch 밀리초). 그 사이 다른 관리자가 고쳤는지 가려내는 데 쓴다
 */
public record AdminNoticeDetailResponse(
    Long noticeId,
    String title,
    String content,
    NoticeCategory category,
    boolean pinned,
    List<Image> images,
    List<Attachment> attachments,
    LocalDateTime createdAt,
    @Schema(description = "수정 시각(KST 기준 epoch 밀리초). 수정 요청의 updatedAt에 그대로 보낸다", example = "1756339200000")
    long updatedAt
) {

    /**
     * 존재하지 않는(삭제된) 파일은 조용히 뺀다. 학생 앱 상세와 같은 정책이다.
     */
    public static AdminNoticeDetailResponse from(AdminNoticeDetail detail) {
        Notice notice = detail.notice();

        return new AdminNoticeDetailResponse(
            notice.getId(),
            notice.getTitle(),
            notice.getContent(),
            notice.getCategory(),
            notice.isPinned(),
            toImages(notice.getImageIds(), detail.imagesById()),
            toAttachments(notice.getAttachmentIds(), detail.attachmentsById()),
            notice.getCreatedAt(),
            DateUtil.toEpochMillis(notice.getUpdatedAt())
        );
    }

    private static List<Image> toImages(List<Long> fileIds, Map<Long, File> filesById) {
        if (fileIds == null) {
            return List.of();
        }
        return fileIds.stream()
            .filter(filesById::containsKey)
            .map(fileId -> Image.of(fileId, filesById.get(fileId)))
            .toList();
    }

    private static List<Attachment> toAttachments(List<Long> fileIds, Map<Long, File> filesById) {
        if (fileIds == null) {
            return List.of();
        }
        return fileIds.stream()
            .filter(filesById::containsKey)
            .map(fileId -> Attachment.of(fileId, filesById.get(fileId)))
            .toList();
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
