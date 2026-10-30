package kr.ac.kookmin.stream.api.admin.event.archive.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveWriteCommand;

/**
 * 아카이빙 등록과 수정이 같은 본문을 받는다. 수정이 전체 필드를 통째로 바꾸는 방식이라 등록과 모양이 같아,
 * 두 record로 나누면 같은 선언이 두 벌 생긴다.
 * <p>
 * 종료일이 시작일보다 앞서는지는 여기서 보지 않는다. 값 하나의 형식이 아니라 두 값 사이의 규칙이라
 * {@code Archive}가 본다.
 *
 * @param endDate        당일 활동이면 생략하거나 {@code null}
 * @param imageIds       활동 사진. 배열 순서가 노출 순서이고 첫 장이 대표 이미지다
 * @param relatedEventId 연결할 행사. 연결하지 않으면 {@code null}이고, 수정에서 {@code null}이면 연결을 끊는다
 */
public record ArchiveWriteRequest(

    @NotBlank(message = "활동 제목을 입력해 주세요.")
    @Size(max = 200, message = "활동 제목은 200자 이하여야 합니다.")
    String title,

    @NotBlank(message = "활동 내용을 입력해 주세요.")
    String content,

    @NotNull(message = "활동 시작일을 입력해 주세요.")
    LocalDate startDate,

    LocalDate endDate,

    @Size(max = 255, message = "활동 장소는 255자 이하여야 합니다.")
    String location,

    @NotBlank(message = "담당 부서를 입력해 주세요.")
    @Size(max = 100, message = "담당 부서는 100자 이하여야 합니다.")
    String departmentName,

    // 사진 없는 아카이빙은 목록에서 썸네일이 빈 칸으로 보인다. 첫 장이 대표 이미지라 한 장은 있어야 한다
    @NotEmpty(message = "활동 사진을 한 장 이상 등록해 주세요.")
    List<@NotNull(message = "파일 ID는 비워 둘 수 없습니다.") Long> imageIds,

    @NotNull(message = "슬랑제 페이지 노출 여부를 입력해 주세요.")
    Boolean isExposed,

    Long relatedEventId
) {

    public ArchiveWriteCommand toCommand() {
        return new ArchiveWriteCommand(
            title, content, startDate, endDate, location, departmentName, distinctImageIds(), isExposed,
            relatedEventId);
    }

    /**
     * 중복을 걷어낸 사진 식별자. 같은 사진을 두 번 보여줄 일이 없고, 파일 존재 확인이 개수를 견주므로 중복이
     * 남아 있으면 있는 파일도 없다고 판단한다. 처음 나온 자리를 지켜 노출 순서는 그대로 둔다.
     */
    private List<Long> distinctImageIds() {
        return imageIds.stream().distinct().toList();
    }
}
