package kr.ac.kookmin.stream.api.app.event.archive.response;

import java.time.LocalDate;
import java.util.List;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveDetail;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveRelatedLink;

public record ArchiveDetailResponse(
    Long archiveId,
    String title,
    LocalDate startDate,
    LocalDate endDate,
    String location,
    String departmentName,
    String content,
    List<Image> images,
    List<RelatedLink> relatedLinks
) {

    public static ArchiveDetailResponse from(ArchiveDetail detail) {
        return new ArchiveDetailResponse(
            detail.archiveId(),
            detail.title(),
            detail.startDate(),
            detail.endDate(),
            detail.location(),
            detail.departmentName(),
            detail.content(),
            detail.imageIds().stream().map(Image::from).toList(),
            detail.relatedLinks().stream().map(RelatedLink::from).toList()
        );
    }

    public record Image(Long fileId, String fileUrl) {

        /**
         * 파일 키 → 공개 URL 조립이 아직 없어 URL은 비어 있다. 조립이 생기면 이 팩토리만 채우면 된다.
         */
        public static Image from(Long fileId) {
            return new Image(fileId, null);
        }
    }

    public record RelatedLink(String title, String url) {

        public static RelatedLink from(ArchiveRelatedLink relatedLink) {
            return new RelatedLink(relatedLink.getTitle(), relatedLink.getUrl());
        }
    }
}
