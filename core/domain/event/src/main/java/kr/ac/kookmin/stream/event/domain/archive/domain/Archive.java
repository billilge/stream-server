package kr.ac.kookmin.stream.event.domain.archive.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
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

    /** 등록 일시는 DB 기본값으로 채워져 저장 전에는 비어 있다. */
    public static Archive create(ArchiveWriteCommand command) {
        validatePeriod(command.startDate(), command.endDate());

        return new Archive(
            null,
            command.title(),
            command.content(),
            command.startDate(),
            command.endDate(),
            command.location(),
            command.departmentName(),
            command.imageIds(),
            command.exposed(),
            command.relatedEventId(),
            null
        );
    }

    /**
     * 전체 필드를 통째로 바꾼다. 사진과 연결 행사도 받은 값으로 교체하므로, 빈 사진 목록은 전체 삭제이고
     * {@code relatedEventId}가 {@code null}이면 연결을 끊는다.
     */
    public void update(ArchiveWriteCommand command) {
        validatePeriod(command.startDate(), command.endDate());

        this.title = command.title();
        this.content = command.content();
        this.startDate = command.startDate();
        this.endDate = command.endDate();
        this.location = command.location();
        this.departmentName = command.departmentName();
        this.imageIds = command.imageIds();
        this.exposed = command.exposed();
        this.relatedEventId = command.relatedEventId();
    }

    /**
     * 노출 여부를 바꾼다. 이미 그 값이어도 그대로 둔다 — 영속성 컨텍스트가 값이 바뀐 행만 UPDATE한다.
     */
    public void changeExposure(boolean exposed) {
        this.exposed = exposed;
    }

    /**
     * 당일 활동은 종료일이 없다. 종료일을 줬다면 시작일보다 앞설 수 없다.
     * <p>
     * 값 하나의 형식이 아니라 두 값 사이의 규칙이라 요청 어노테이션으로는 표현할 수 없어 도메인에서 본다.
     */
    private static void validatePeriod(LocalDate startDate, LocalDate endDate) {
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new BusinessException(ArchiveErrorCode.INVALID_ARCHIVE_PERIOD);
        }
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
