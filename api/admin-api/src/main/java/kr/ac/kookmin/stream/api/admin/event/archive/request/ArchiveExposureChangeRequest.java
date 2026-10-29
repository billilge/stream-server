package kr.ac.kookmin.stream.api.admin.event.archive.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 여러 아카이빙의 슬랑제 페이지 노출 여부를 한 번에 바꾼다.
 * <p>
 * 목록의 체크박스 일괄 변경과 행별 드롭다운 변경이 같은 동작이라 하나로 받는다 — 행별 변경은 식별자가 하나인 요청이다.
 */
public record ArchiveExposureChangeRequest(

    // 목록에서 체크박스로 고르는 동작이라 한 번에 고를 수 있는 최대는 한 페이지 최대와 같다(PageParams의 size 상한)
    @Size(min = 1, max = 100, message = "아카이빙을 1개 이상 100개 이하로 선택해 주세요.")
    List<@NotNull(message = "아카이빙 ID는 비워 둘 수 없습니다.") Long> archiveIds,

    @NotNull(message = "슬랑제 페이지 노출 여부를 입력해 주세요.")
    Boolean isExposed
) {

    /** 중복을 걷어낸 아카이빙 식별자. 서비스가 "요청한 수와 조회된 수"를 견줘 없는 아카이빙을 가려내기 때문이다. */
    public List<Long> distinctArchiveIds() {
        return archiveIds.stream().distinct().toList();
    }
}
