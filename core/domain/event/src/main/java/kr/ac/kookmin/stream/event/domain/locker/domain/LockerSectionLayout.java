package kr.ac.kookmin.stream.event.domain.locker.domain;

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

    private Long id;
    private Long sectionId;
    /** root 블록 트리 JSON 원문. 서버는 해석하지 않고 그대로 내려준다. */
    private String layout;
    /** layout 형식 버전. 블록 종류·필드가 바뀌면 올려서 옛 앱이 모르는 형식을 알아채게 한다. */
    private int version;
    private Long photoFileId;

    public static LockerSectionLayout of(Long id, Long sectionId, String layout, int version, Long photoFileId) {
        return new LockerSectionLayout(id, sectionId, layout, version, photoFileId);
    }
}
