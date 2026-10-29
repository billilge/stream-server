package kr.ac.kookmin.stream.event.domain.archive.domain;

import java.time.LocalDate;
import java.util.List;

/**
 * 아카이빙 수정 입력. 전체 필드를 받아 통째로 바꾼다.
 *
 * @param endDate        당일 활동이면 {@code null}
 * @param imageIds       활동 사진. 배열 순서가 노출 순서이고 첫 장이 대표 이미지다
 * @param relatedEventId 연결할 행사. {@code null}이면 연결을 끊는다
 */
public record ArchiveUpdateCommand(
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
