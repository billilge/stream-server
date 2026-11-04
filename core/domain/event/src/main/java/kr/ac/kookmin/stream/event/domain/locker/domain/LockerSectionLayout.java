package kr.ac.kookmin.stream.event.domain.locker.domain;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 구역 상세 화면의 칸 배치 구조와 구역 사진. 구역마다 하나다.
 * <p>
 * 배치 구조에는 칸 위치와 창문·벽면 같은 고정 구조만 담는다. 선택 가능 여부처럼 신청할 때마다 바뀌는 칸 상태는
 * 조회 시점에 사물함에서 계산하고, 둘은 사물함 번호({@link Locker#getLockerNumber()})로 잇는다.
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LockerSectionLayout {

    /**
     * 지금 서버가 쓰는 배치 형식 버전. 블록 종류·필드가 바뀌면 올린다.
     * <p>
     * 운영진이 화면에서 고르는 값이 아니다. 형식 버전은 서버와 앱 사이의 약속이라 저장할 때 서버가 박는다.
     */
    public static final int CURRENT_VERSION = 1;

    private Long id;
    private Long sectionId;
    /** root 블록 트리 JSON 원문. 서버는 해석하지 않고 그대로 내려준다. */
    private String layout;
    /** layout 형식 버전. 블록 종류·필드가 바뀌면 올려서 옛 앱이 모르는 형식을 알아채게 한다. */
    private int version;
    /** 구역 실제 사진. 배열 순서가 노출 순서다. 등록하지 않았으면 빈 목록이다. */
    private List<Long> photoFileIds;

    public static LockerSectionLayout of(
        Long id,
        Long sectionId,
        String layout,
        int version,
        List<Long> photoFileIds
    ) {
        return new LockerSectionLayout(id, sectionId, layout, version, photoFileIds);
    }

    /**
     * 배치 구조를 아직 등록하지 않은 구역에 쓸 빈 행.
     * <p>
     * 배치 JSON과 형식 버전은 {@link #changeLayout(String)}이 함께 채운다 — 두 값을 넣는 자리를 한 곳으로 모은다.
     * 사진은 따로 올리므로 빈 목록으로 시작한다.
     */
    public static LockerSectionLayout createEmpty(Long sectionId) {
        return new LockerSectionLayout(null, sectionId, null, 0, List.of());
    }

    /**
     * 배치 구조를 바꾼다. 형식 버전도 지금 버전으로 맞춘다 — 저장되는 JSON은 현재 콘솔이 만든 형식이다.
     */
    public void changeLayout(String layout) {
        this.layout = layout;
        this.version = CURRENT_VERSION;
    }

    /**
     * 구역 사진을 받은 목록으로 통째로 바꾼다. 배열 순서가 노출 순서다.
     * <p>
     * 화면이 사진을 지우고 추가한 뒤 저장 버튼 하나로 올리므로 개별 추가·삭제가 아니라 전체 교체다.
     */
    public void changePhotos(List<Long> photoFileIds) {
        this.photoFileIds = photoFileIds;
    }
}
