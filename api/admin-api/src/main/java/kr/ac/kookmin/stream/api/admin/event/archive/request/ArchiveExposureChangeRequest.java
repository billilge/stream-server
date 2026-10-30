package kr.ac.kookmin.stream.api.admin.event.archive.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 여러 아카이빙의 슬랑제 페이지 노출 여부를 한 번에 바꾼다.
 * <p>
 * 목록의 체크박스 일괄 변경과 행별 드롭다운 변경이 같은 동작이라 하나로 받는다 — 행별 변경은 식별자가 하나인 요청이다.
 */
public record ArchiveExposureChangeRequest(

    // @Size는 null을 통과시키므로 @NotEmpty가 같이 있어야 목록을 빠뜨린 요청이 400으로 나간다.
    // 상한은 목록에서 체크박스로 고르는 동작이라 한 페이지 최대와 같게 둔다(PageParams의 size 상한)
    @NotEmpty(message = "아카이빙을 하나 이상 선택해 주세요.")
    @Size(max = 100, message = "아카이빙은 한 번에 100개까지 바꿀 수 있습니다.")
    List<@NotNull(message = "아카이빙 ID는 비워 둘 수 없습니다.") Long> archiveIds,

    @NotNull(message = "슬랑제 페이지 노출 여부를 입력해 주세요.")
    Boolean isExposed
) {
}
