package kr.ac.kookmin.stream.api.app.event.archive.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveDetail;
import kr.ac.kookmin.stream.file.domain.File;

public record ArchiveDetailResponse(
    Long archiveId,
    String title,
    LocalDate startDate,
    LocalDate endDate,
    String location,
    String departmentName,
    String content,
    List<Image> images
) {

    /**
     * 존재하지 않는(삭제된) fileId는 imagesById에 키가 없다 — 그런 항목은 목록에서 조용히 제외한다.
     */
    public static ArchiveDetailResponse from(ArchiveDetail detail, Map<Long, File> imagesById) {
        return new ArchiveDetailResponse(
            detail.archiveId(),
            detail.title(),
            detail.startDate(),
            detail.endDate(),
            detail.location(),
            detail.departmentName(),
            detail.content(),
            detail.imageIds().stream()
                .filter(imagesById::containsKey)
                .map(fileId -> Image.of(fileId, imagesById.get(fileId)))
                .toList()
        );
    }

    public record Image(Long fileId, String fileUrl) {

        public static Image of(Long fileId, File file) {
            return new Image(fileId, StorageUrlBuilder.build(file.getFileKey()));
        }
    }
}
