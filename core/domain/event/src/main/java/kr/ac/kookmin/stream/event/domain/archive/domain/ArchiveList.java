package kr.ac.kookmin.stream.event.domain.archive.domain;

import java.util.List;

/**
 * 아카이빙 목록 조회 결과. 연도 목록은 연도 필터와 무관하게 항상 전체를 담는다.
 */
public record ArchiveList(List<ArchiveSummary> archives, List<Integer> years) {
}
