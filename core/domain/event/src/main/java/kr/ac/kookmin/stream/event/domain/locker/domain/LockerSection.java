package kr.ac.kookmin.stream.event.domain.locker.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 사물함 구역. 사물함을 묶는 단위이자 구역 목록·구역 상세 조회의 기준이다.
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LockerSection {

    private Long id;
    private String label;

    public static LockerSection of(Long id, String label) {
        return new LockerSection(id, label);
    }
}
