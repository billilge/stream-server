package kr.ac.kookmin.stream.event.domain.locker.domain;

import java.util.List;

/**
 * 사물함 구역 상세 한 건. 칸 선택 화면이 구역 정보·배치 구조·사물함을 한 번에 받는다.
 *
 * @param layout  배치 구조와 사진. 아직 등록하지 않은 구역이면 {@code null}
 * @param lockers 구역에 속한 사물함. 선택 가능 여부는 담지 않는다
 */
public record LockerSectionDetail(
    LockerSection section,
    LockerSectionLayout layout,
    List<Locker> lockers
) {
}
