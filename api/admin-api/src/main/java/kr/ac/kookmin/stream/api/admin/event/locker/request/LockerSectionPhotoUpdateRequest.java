package kr.ac.kookmin.stream.api.admin.event.locker.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 구역 사진을 받은 목록으로 통째로 바꾼다.
 * <p>
 * 화면이 사진을 지우고 추가한 뒤 저장 버튼 하나로 올리므로 개별 추가·삭제가 아니라 전체 교체다.
 */
public record LockerSectionPhotoUpdateRequest(
    @NotEmpty(message = "구역 사진을 한 장 이상 등록해 주세요.")
    List<@NotNull(message = "파일 ID는 비워 둘 수 없습니다.") Long> photoFileIds
) {

    /**
     * 중복을 걷어낸 파일 식별자. 같은 사진이 두 번 와도 한 번만 보여주면 되고, 파일 존재 확인이 개수를 견주므로
     * 중복이 남아 있으면 있는 파일인데도 없다고 판단한다. 처음 나온 자리를 지켜 노출 순서는 그대로 둔다.
     */
    public List<Long> distinctPhotoFileIds() {
        return photoFileIds.stream().distinct().toList();
    }
}
