package kr.ac.kookmin.stream.event.domain.archive.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ArchiveRelatedLink {

    private Long id;
    private Long archiveId;
    private String title;
    private String url;
    private int displayOrder;

    public static ArchiveRelatedLink of(
        Long id,
        Long archiveId,
        String title,
        String url,
        int displayOrder
    ) {
        return new ArchiveRelatedLink(id, archiveId, title, url, displayOrder);
    }
}
