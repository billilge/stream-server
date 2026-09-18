package kr.ac.kookmin.stream.event.domain.archive.domain;

import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Archive {

    private Long id;
    private String title;
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private String departmentName;
    private List<Long> imageIds;

    public static Archive of(
        Long id,
        String title,
        String content,
        LocalDate startDate,
        LocalDate endDate,
        String location,
        String departmentName,
        List<Long> imageIds
    ) {
        return new Archive(id, title, content, startDate, endDate, location, departmentName, imageIds);
    }

    /**
     * 대표 이미지 파일 id. 사진 배열의 첫 장을 대표로 쓴다.
     * <p>
     * 사진 없는 아카이빙은 등록되지 않는다는 전제지만 최소 1장 제약이 없어, 비어 있으면 예외 대신 null을 돌린다.
     */
    public Long thumbnailFileId() {
        return imageIds == null || imageIds.isEmpty() ? null : imageIds.getFirst();
    }
}
