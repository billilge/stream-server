package kr.ac.kookmin.stream.event.domain.archive.domain;

import java.time.LocalDate;
import java.util.List;

/**
 * 아카이빙 등록·수정 입력. 수정이 전체 필드를 통째로 바꾸는 방식이라 등록과 담는 값이 같아 하나로 둔다.
 *
 * @param endDate        당일 활동이면 {@code null}
 * @param imageIds       활동 사진. 배열 순서가 노출 순서이고 첫 장이 대표 이미지다
 * @param relatedEventId 연결할 행사. 연결하지 않으면 {@code null}이고, 수정에서 {@code null}이면 연결을 끊는다
 */
public record ArchiveWriteCommand(
    String title,
    String content,
    LocalDate startDate,
    LocalDate endDate,
    String location,
    String departmentName,
    List<Long> imageIds,
    boolean exposed,
    Long relatedEventId
) {
}
