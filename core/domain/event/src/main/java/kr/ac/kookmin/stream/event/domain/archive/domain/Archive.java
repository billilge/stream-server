package kr.ac.kookmin.stream.event.domain.archive.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    /** 슬랑제(연말 행사) 전용 페이지에 띄울지. 삭제 여부와 달리 운영진이 목록에서 보고 켜고 끄는 값이라 도메인이 들고 있다. */
    private boolean exposed;
    /** 연결한 행사. 연결하지 않았으면 {@code null}이다. 제목은 담지 않는다 — 조회 시점에 행사에서 가져온다. */
    private Long relatedEventId;
    /** 등록 일시. 운영진 목록이 이 순서로 정렬하고 화면에도 보여준다. */
    private LocalDateTime createdAt;

    public static Archive of(
        Long id,
        String title,
        String content,
        LocalDate startDate,
        LocalDate endDate,
        String location,
        String departmentName,
        List<Long> imageIds,
        boolean exposed,
        Long relatedEventId,
        LocalDateTime createdAt
    ) {
        return new Archive(id, title, content, startDate, endDate, location, departmentName, imageIds,
            exposed, relatedEventId, createdAt);
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
